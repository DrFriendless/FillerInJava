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
 * Mainoumi is deliberately obstructive. He chooses the colour which will stop
 * his opponent from scoring the most.
 * Named for the sumo wrestler known as the Mighty Mite.
 *
 * @author John Farrell
 */
public class Mainoumi extends RobotPlayer {
    public String getName() { return "Mainoumi"; }

    public int turn() {
        int attempt = mostIfWinTurn();
        if (attempt < 0) {
            attempt = opponentMostTurn();
            if (attempt == colour) attempt = expandTurn();
        }
        if (attempt < 0) attempt = mostTurn();
        return attempt;
    }

    public String getIcon() { return "greenAlien.gif"; }
}
