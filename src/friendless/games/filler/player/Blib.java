package friendless.games.filler.player;

import friendless.games.filler.*;

import java.util.BitSet;

/**
 * Created by john on 4/09/15.
 */
@SuppressWarnings("unused")
public class Blib extends OptimalRobotPlayer {
    public static final int CENTRE = FillerModel.makeIndex(FillerSettings.COLUMNS / 2, FillerSettings.ROWS / 2);
    protected int[] goals;

    public String getName() { return "Blib"; }

    public int turn() {
        if (goals == null) {
            goals = new int[3];
            goals[0] = CENTRE;
            goals[1] = makeIndex((getX(origins[0]) * 2 + getX(origins[1]))/3,getY(origins[1]));
            goals[2] = makeIndex(getX(origins[1]),(getY(origins[0]) + getY(origins[1]))/3);
        }
        int attempt = mostIfWinTurn();
        if (attempt < 0) attempt = mostBestGoalColour(goals[0]);
        if (attempt < 0) {
            int d1 = space.distance[goals[1]];
            int d2 = space.distance[goals[2]];
            if (d1 > 0 || d2 > 0) {
                boolean swap = (d1 <= 0) || (d2 >= 0 && d2 < d1);
                if (swap) {
                    int temp = goals[1];
                    goals[1] = goals[2];
                    goals[2] = temp;
                }
                attempt = mostBestGoalColour(goals[1]);
                if (attempt < 0) attempt = mostBestGoalColour(goals[2]);
            }
        }
        if (attempt < 0) attempt = expandTurn();
        if (attempt < 0) attempt = mostFreeTurn();
        if (attempt < 0) attempt = mostTurn();
        return attempt;
    }

    protected int mostBestGoalColour(int goal) {
        BitSet bestGoalColours = getBestGoalColours(goal);
        bestGoalColours.clear(myColour);
        bestGoalColours.clear(otherPlayerColour);
        BitSet allowed = (BitSet) NO_COLOURS.clone();
        allowed.set(FillerModel.BORDER);
        allowed.set(FillerModel.SHARED_BORDER);
        int[] count = countSet(allowed);
        // choose any of the best colours that get us to the goal quickest.
        BitSet favourites = null;
        int best = 0;
        for (int i=0; i<FillerSettings.NUM_COLOURS; i++) {
            if (count[i] <= 0 || !bestGoalColours.get(i)) continue;
            if (count[i] > best) {
                favourites = (BitSet) NO_COLOURS.clone();
                favourites.set(i);
                best = count[i];
            } else if (count[i] == best) {
                favourites.set(i);
            }
        }
        return chooseRandom(favourites);
    }

    public String getIcon() { return "blib.png"; }
}

