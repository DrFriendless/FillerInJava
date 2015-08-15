package friendless.games.filler;

/**
 * Created by john on 15/08/15.
 */
public class ExpandEvaluator implements Evaluator {
    public int eval(FillerModel model, FillerPlayerSpace space, int[] origins) {
        int furthest = Integer.MIN_VALUE;
        int[] counted = space.counted;
        for (int i=0; i<counted.length; i++) {
            if (counted[i] == FillerModel.BORDER || counted[i] == FillerModel.SHARED_BORDER) {
                int dist = sideDistance(origins[0], i);
                if (dist > furthest) furthest = dist;
            }
        }
        return furthest;
    }

    protected static int sideDistance(int p1, int p2) {
        return Math.abs(getX(p1)-getX(p2)) + Math.abs(getY(p1)-getY(p2));
    }

    /** Get the the X coordinate of piece <code>i</code>. */
    protected static int getX(int i) { return FillerModel.getX(i); }

    /** Get the the Y coordinate of piece <code>i</code>. */
    protected static int getY(int i) { return FillerModel.getY(i); }
}
