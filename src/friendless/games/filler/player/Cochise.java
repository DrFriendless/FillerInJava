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
 * Cochise expands aggressively towards the opponent's origin.
 * When he gets to 400 points, he starts playing like Che.
 *
 * @author John Farrell
 */
public class Cochise extends RobotPlayer {
    public String getName() { return "Cochise"; }

    public int turn() {
        int attempt;
        if (score < 400) {
            attempt = expandTurn();
        } else {
            attempt = furthestBorderTurn();
            if (attempt < 0) attempt = mostFreeTurn();
        }
        if (attempt < 0) attempt = mostTurn();
        return attempt;
    }
}
