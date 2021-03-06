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
import java.util.*;

/**
 * An OptimalFillerPlayer which takes the shortest route to the centre of the board.
 * If the centre of the board is taken, just occupies as much space as possible.
 * Named for Dieter Dengler.
 *
 * @author John Farrell
 */
public class Dieter extends OptimalRobotPlayer {
    public static final int GOAL = makeIndex(FillerSettings.COLUMNS / 2, FillerSettings.ROWS / 2);

    public String getName() { return "Dieter"; }

    public int turn() {
        BitSet colours = getBestGoalColours(GOAL);
        colours.clear(myColour);
        colours.clear(otherPlayerColour);
        int attempt = chooseRandom(colours);
        if (attempt < 0) attempt = smartMostTurn();
        return attempt;
    }
}
