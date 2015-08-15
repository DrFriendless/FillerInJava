package friendless.games.filler.player;

import friendless.games.filler.FillerModel;
import friendless.games.filler.FillerSettings;
import friendless.games.filler.VotingRobotPlayer;

/**
 * Similar to Jefferson but he considers more options.
 * Named for G.K. Chesterton who said "The business of Progressives is to go on making mistakes. The business of the
 * Conservatives is to prevent the mistakes from being corrected."
 *
 * @author John Farrell
 */
public class Chesterton extends VotingRobotPlayer {
    public static final int target = FillerModel.makeIndex(FillerSettings.COLUMNS / 2, FillerSettings.ROWS / 2);

    public String getName() { return "Chesterton"; }

    public int turn() {
        int attempt = mostIfWinTurn();
        if (attempt >= 0) return attempt;
        int[] votes = new int[FillerSettings.NUM_COLOURS];
        addVote(votes, furthestBorderTurn());
        addVote(votes, smartMostTurn());
        addVote(votes, expandTurn());
        addVote(votes, targetTurn(origins[1]));
        addVote(votes, targetTurn(target));
        addVote(votes, opponentMostTurn());
        return votersChoice(votes);
    }
    public String getIcon() { return "blueAlien.gif"; }
}
