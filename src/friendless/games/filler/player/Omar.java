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

package friendless.games.filler.player;

import friendless.games.filler.*;

/**
 * Omar tries to maximise the distance from the origin of the pieces he takes.
 *
 * @author John Farrell
 */
public class Omar extends LookaheadRobotPlayer {
    Evaluator eval = new ExpandEvaluator();

    public String getName() { return "Omar"; }

    /** Chooses a colour to get the most points immediately **/
    public int turn() {
        int attempt = lookahead(eval);
        if (attempt < 0) attempt = mostTurn();
        return attempt;
    }
}
