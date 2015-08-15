package friendless.games.filler;

/**
 * Created by john on 15/08/15.
 */
public class DistanceEvaluator implements Evaluator {
    private static final int INACCESSIBLE = 50;
    @Override
    // TODO - if I choose a particular colour, the opponent can't choose that colour on their next turn making
    // the distance one more for pieces of that colour on their border.
    public int eval(FillerModel model, FillerPlayerSpace space, int[] origins) {
        int myDistance = 0;
        for (int d : space.distance) {
            if (d == FillerModel.UNREACHABLE_DISTANCE) {
                myDistance += INACCESSIBLE;
            } else {
                myDistance += d;
            }
        }
        int hisDistance = 0;
        for (int d : space.opponentDistance) {
            if (d == FillerModel.UNREACHABLE_DISTANCE) {
                hisDistance += INACCESSIBLE;
            } else {
                hisDistance += d;
            }
        }
        return hisDistance - myDistance;
    }
}
