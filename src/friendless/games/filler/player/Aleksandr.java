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
 * Aleksandr uses a quite sophisticated algorithm.
 * He starts by expanding as fast as possible, then when he reaches the other
 * player he chooses the emptiest corner of the board and tries to occupy that.
 * When he thinks he is going to win, he does so as fast as possible.
 *
 * @author John Farrell
 */
public final class Aleksandr extends RobotPlayer {
    enum Phase { NOT_TOUCHING, SHARED_BORDER, END_GAME }

    private Phase phase = Phase.NOT_TOUCHING;

    public String getName() { return "Aleksandr"; }

    public int turn() {
        int attempt = -1;
        int[] counted = space.counted;
        int[] typeCount = new int[FillerModel.NUM_TYPES];
        for (int c : counted) typeCount[c]++;
        if (phase == Phase.NOT_TOUCHING && typeCount[FillerModel.SHARED_BORDER] > 0) {
            phase = Phase.SHARED_BORDER;
        }
        if (phase == Phase.SHARED_BORDER && (realScore >= FillerSettings.POINTS_TO_WIN || typeCount[FillerModel.SHARED_BORDER] == 0)) {
            phase = Phase.END_GAME;
        }
        switch (phase) {
            case NOT_TOUCHING:
                attempt = expandTurn();
                break;
            case SHARED_BORDER:
                attempt = targetTurn(calcTarget(counted));
                break;
            case END_GAME:
                attempt = mostFreeTurn();
                break;
        }
        if (attempt < 0) attempt = mostTurn();
        return attempt;
    }

    int calcTarget(int[] counted) {
        int[] corners = new int[4];
        int[] targets = {
                FillerModel.makeIndex(FillerSettings.COLUMNS/4,FillerSettings.ROWS/4),
                FillerModel.makeIndex(FillerSettings.COLUMNS/4,FillerSettings.ROWS*3/4),
                FillerModel.makeIndex(FillerSettings.COLUMNS*3/4,FillerSettings.ROWS/4),
                FillerModel.makeIndex(FillerSettings.COLUMNS*3/4,FillerSettings.ROWS*3/4)
        };
        for (int i=0; i<counted.length; i++) {
            if (counted[i] == FillerModel.FREE) {
                int x = getX(i);
                int y = getY(i);
                int xh = (x < FillerSettings.COLUMNS/2) ? 0 : 1;
                int yh = (y < FillerSettings.ROWS/2) ? 0 : 1;
                corners[xh*2 + yh]++;
            }
        }
        int highest = Integer.MIN_VALUE;
        int fave = -1;
        for (int i=0; i<4; i++) {
            if (corners[i] > highest) {
                highest = corners[i];
                fave = i;
            }
        }
        return targets[fave];
    }

    public String getIcon() { return "blueAlien.gif"; }
}
