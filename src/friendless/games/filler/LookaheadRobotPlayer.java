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
    public int lookahead(Evaluator evaluator) {
        int[] pieces = copy(model.pieces);
        BitSet results = (BitSet) NO_COLOURS.clone();
        int highest = Integer.MIN_VALUE;
        for (int c=0; c<FillerSettings.NUM_COLOURS; c++) {
            if (c == otherPlayerColour || c == colour) continue;
            FillerModel model = new FillerModel(pieces);
            // pretend we took colour c
            int[] counted = space.counted;
            calculate(model);
            for (int i=0; i<counted.length; i++) {
                if (counted[i] == FillerModel.MINE) model.pieces[i] = c;
            }
            calculate(model);
            // now which gets us furthest from our origin?
            int score = evaluator.eval(model, counted);
            if (score == highest) {
                results.set(c);
            } else if (score > highest) {
                results = (BitSet) NO_COLOURS.clone();
                results.set(c);
                highest = score;
            }
        }
        int choice = chooseRandom(results);
        return choice;
    }

    public class ExpandEvaluator implements Evaluator {
        public int eval(FillerModel model, int[] counted) {
            int furthest = Integer.MIN_VALUE;
            for (int i=0; i<counted.length; i++) {
                if ((counted[i] == FillerModel.BORDER) || (counted[i] == FillerModel.SHARED_BORDER)) {
                    int dist = sideDistance(origins[0], i);
                    if (dist > furthest) furthest = dist;
                }
            }
            return furthest;
        }
    }

    protected BitSet maximise(Evaluator evaluator, int level) {
        int[] counted = space.counted;
        return maximise(evaluator,allUsefulColours(),model,counted);
    }

    public String getIcon() { return "badrock.png"; }
}
