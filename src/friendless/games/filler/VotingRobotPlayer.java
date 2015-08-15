package friendless.games.filler;

/**
 * Created by john on 15/08/15.
 */
public abstract class VotingRobotPlayer extends RobotPlayer {
    protected int votersChoice(int[] votes) {
        int best = -1;
        int bestVotes = -1;
        votes[myColour] = -1;
        for (int i=0; i<votes.length; i++) {
            if (votes[i] > bestVotes) {
                bestVotes = votes[i];
                best = i;
            }
        }
        return best;
    }

    protected void addVote(int[] votes, int vote) {
        if (vote < 0) return;
        votes[vote]++;
    }
}
