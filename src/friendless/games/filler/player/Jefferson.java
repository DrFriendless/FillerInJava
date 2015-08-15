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
 * Jefferson tries multiple strategies, and takes the one which gets the most votes.
 * Named for Thomas Jefferson and William Jefferson Clinton.
 *
 * @author John Farrell
 */
public final class Jefferson extends VotingRobotPlayer {
    public static final int target = FillerModel.makeIndex(FillerSettings.COLUMNS / 2, FillerSettings.ROWS / 2);

    public String getName() { return "Jefferson"; }

    public int turn() {
        int attempt = mostIfWinTurn();
        if (attempt >= 0) return attempt;
        int[] votes = new int[FillerSettings.NUM_COLOURS];
        addVote(votes, furthestBorderTurn());
        addVote(votes, mostTurn());
        addVote(votes, expandTurn());
        addVote(votes, targetTurn(origins[1]));
        addVote(votes, targetTurn(target));
        return votersChoice(votes);
    }

    public String getIcon() { return "blueAlien.gif"; }
}
