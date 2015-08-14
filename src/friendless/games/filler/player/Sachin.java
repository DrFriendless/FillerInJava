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
 * An OptimalFillerPlayer which combines the strategies of Dieter and Wanda.
 * Named for Sachin Tendulkar.
 *
 * @author John Farrell
 */
public class Sachin extends OptimalRobotPlayer {
    public static final int CENTRE = FillerModel.makeIndex(FillerSettings.COLUMNS / 2, FillerSettings.ROWS / 2);
    protected int[] goals;

    public String getName() { return "Sachin"; }

    public int turn() {
        if (goals == null) {
            goals = new int[3];
            goals[0] = CENTRE;
            goals[1] = makeIndex((getX(origins[0]) * 2 + getX(origins[1]))/3,getY(origins[1]));
            goals[2] = makeIndex(getX(origins[1]),(getY(origins[0]) + getY(origins[1]))/3);
        }
        int attempt = mostIfWinTurn();
        if (attempt < 0) attempt = anyBestGoalColour(goals[0]);
        if (attempt < 0) {
            int d1 = space.distance[goals[1]];
            int d2 = space.distance[goals[2]];
            if (d1 > 0 || d2 > 0) {
                boolean swap = (d1 <= 0) || (d2 >= 0 && d2 < d1);
                if (swap) {
                    int temp = goals[1];
                    goals[1] = goals[2];
                    goals[2] = temp;
                }
                attempt = anyBestGoalColour(goals[1]);
                if (attempt < 0) attempt = anyBestGoalColour(goals[2]);
            }
        }
        if (attempt < 0) attempt = expandTurn();
        if (attempt < 0) attempt = mostFreeTurn();
        if (attempt < 0) attempt = mostTurn();
        return attempt;
    }

    protected int anyBestGoalColour(int goal) {
        BitSet colours = getBestGoalColours(goal);
        colours.clear(colour);
        colours.clear(otherPlayerColour);
        int attempt = chooseRandom(colours);
        return attempt;
    }
}
