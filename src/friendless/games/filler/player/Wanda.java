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
 * Wanda chooses three strategic locations on the board - the centre, and
 * completely across the board in 2 directions. She attempts to take those
 * locations. If she achieves that, she expands as much as possible. This has
 * the effect of stretching her across the board, then expanding explosively.
 *
 * @author John Farrell
 */
public final class Wanda extends RobotPlayer {
    public static final int centre = FillerModel.makeIndex(FillerSettings.COLUMNS / 2, FillerSettings.ROWS / 2);
    protected int[] goals;

    public String getName() { return "Wanda"; }

    public int turn() {
        if (goals == null) {
            goals = new int[3];
            goals[0] = centre;
            goals[1] = makeIndex((getX(origins[0]) * 2 + getX(origins[1]))/3,getY(origins[1]));
            goals[2] = makeIndex(getX(origins[1]),(getY(origins[0]) + getY(origins[1]))/3);
        }
        int attempt = goalTurn(goals[0]);
        if (attempt < 0) attempt = goalTurn(goals[1]);
        if (attempt < 0) attempt = goalTurn(goals[2]);
        if (attempt < 0) attempt = expandTurn();
        if (attempt < 0) attempt = smartMostTurn();
        return attempt;
    }
}
