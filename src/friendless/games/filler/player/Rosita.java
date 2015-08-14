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

import java.awt.*;
import friendless.games.filler.*;

/**
 * Rosita aims to occupy the two spare corners of the board, then fill in the
 * remaining spaces.
 *
 * @author John Farrell
 */
public final class Rosita extends RobotPlayer {
    protected int[] goals;

    public String getName() { return "Rosita"; }

    public int turn() {
        if (goals == null) {
            goals = new int[2];
            goals[0] = makeIndex(getX(origins[1]),getY(origins[0]));
            goals[1] = makeIndex(getX(origins[0]),getY(origins[1]));
        }
        int attempt = mostIfWinTurn();
        if (attempt < 0) attempt = goalTurn(goals[0]);
        if (attempt < 0) attempt = goalTurn(goals[1]);
        if (attempt < 0) attempt = mostTurn();
        return attempt;
    }
}
