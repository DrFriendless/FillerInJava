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
        int highest = Integer.MIN_VALUE;
        for (int c=0; c<FillerSettings.NUM_COLOURS; c++) {
            if (c == otherPlayerColour || c == myColour) continue;
            FillerModel model = new FillerModel(pieces);
            // pretend we took colour c
            int[] counted = space.counted;
            calculate(model, false, false);
            for (int i=0; i<counted.length; i++) {
                if (counted[i] == FillerModel.MINE) model.pieces[i] = c;
            }
            calculate(model, distance, opponentDistance);
            // now how much does that score?
            int score = evaluator.eval(model, space, origins);
            if (score == highest) {
                results.set(c);
            } else if (score > highest) {
                results = (BitSet) NO_COLOURS.clone();
                results.set(c);
                highest = score;
            }
        }
        return chooseRandom(results);
    }

    protected void calculate(FillerModel model, boolean distance, boolean opponentDistance) {
        FillerModel.allocateTypes(model, origins, space);
        if (distance) FillerModel.allocateDistance(model, space);
        if (opponentDistance) FillerModel.allocateOpponentDistance(model, space);
    }

    public String getIcon() { return "badrock.png"; }
}
