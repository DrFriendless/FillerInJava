//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Library General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

package friendless.games.filler;

import java.util.*;

/**
 * A robot player which uses lookahead and evaluators to implement its
 * strategy.
 *
 * @author John Farrell
 */
abstract public class LookaheadRobotPlayer extends RobotPlayer {
    public int lookahead(Evaluator evaluator, boolean distance, boolean opponentDistance) {
        int[] pieces = copy(model.pieces);
        BitSet results = (BitSet) NO_COLOURS.clone();
        int[] colours = new int[FillerSettings.NUM_COLOURS];
        for (int c=0; c<FillerSettings.NUM_COLOURS; c++) colours[c] = c;

        Optional<int[]> best = Arrays.stream(colours).
                filter(c -> c != otherPlayerColour && c != myColour).
                parallel().
                mapToObj(c -> eval(pieces, c, evaluator, distance, opponentDistance)).
                reduce((pair1, pair2) -> (pair1[1] > pair2[1]) ? pair1 : pair2);
        if (best.isPresent() && best.get()[1] > Integer.MIN_VALUE) return best.get()[0];
        return chooseRandom(results);
    }

    int[] eval(int[] pieces, int c, Evaluator evaluator, boolean distance, boolean opponentDistance) {
        FillerModel model = new FillerModel(pieces);
        // pretend we took colour c
        int[] counted = space.counted;
        // need a thread-local space in case we are doing this in parallel.
        FillerPlayerSpace space = new FillerPlayerSpace();
        calculate(model, false, false, space);
        for (int i=0; i<counted.length; i++) {
            if (counted[i] == FillerModel.MINE) model.pieces[i] = c;
        }
        calculate(model, distance, opponentDistance, space);
        // now how much does that score?
        int score = evaluator.eval(model, space, origins);
        return new int[] { c, score };
    }

    protected void calculate(FillerModel model, boolean distance, boolean opponentDistance, FillerPlayerSpace space) {
        FillerModel.allocateTypes(model, origins, space);
        if (distance) FillerModel.allocateDistance(model, space);
        if (opponentDistance) FillerModel.allocateOpponentDistance(model, space);
    }

    public String getIcon() { return "badrock.png"; }
}
