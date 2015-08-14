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
 * Manuelito always prefers to take the border.
 *
 * @author John Farrell
 */
public final class Manuelito extends RobotPlayer {
    boolean toggle;

    public String getName() { return "Manuelito"; }

    public int turn() {
        int attempt = borderTurn();
        if (attempt < 0) attempt = expandTurn();
        if (attempt < 0) attempt = mostTurn();
        return attempt;
    }

    public String getIcon() { return "greenAlien.gif"; }
}
