package friendless.games.filler.player;

import friendless.games.filler.DistanceEvaluator;
import friendless.games.filler.LookaheadRobotPlayer;

/**
 * Helen calculates the distances to all points she could possibly capture, subtracts the distances to all points
 * you could possibly capture, and chooses the colour which minimises that.
 *
 * Created by john on 15/08/15.
 */
@SuppressWarnings("unused")
public class Helen extends LookaheadRobotPlayer {
    @Override
    public String getName() {
        return "Helen";
    }

    @Override
    public int turn() {
        int c = mostIfWinTurn();
        if (noUndecidedSpaces()) return mostTurn();
        if (c < 0) c = lookahead(new DistanceEvaluator(), true, true);
        return c;
    }

    public String getIcon() { return "brainhead.png"; }
}
