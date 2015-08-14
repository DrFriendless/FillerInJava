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

package friendless.games.filler;

import java.io.*;
import java.util.*;

/**
 * Rating information for a player.
 * Includes the player's ELO rating and their head to head record.
 *
 * @author John Farrell
 */
public class PlayerRating implements Serializable {
    static final long serialVersionUID = 734940228562653290L;
    public String name;
    public int rating;
    public int games;
    public Map headToHead;

    // for serialisation
    public PlayerRating() { }

    public PlayerRating(String fullName) {
        this.name = fullName;
        this.rating = EloRating.INITIAL;
        this.games = 0;
        this.headToHead = new HashMap();
    }

    /** @return the head to head record of this player against <code>player</code>. */
    public int[] getRecordAgainst(PlayerWrapper player) {
        int[] rec = (int[]) headToHead.get(player.getFullName());
        if (rec == null) {
            rec = new int[] { 0, 0 };
        } else {
            rec = (int[]) rec.clone();
        }
        return rec;
    }

    public String getShortHeadToHead(PlayerWrapper player) {
        String oppName = player.getFullName();
        int[] record = (int[])headToHead.get(oppName);
        if (record == null) record = new int[] { 0, 0 };
        return (record[0] + "-" + record[1]);
    }

    public String getHeadToHead(String name, PlayerWrapper player) {
        String oppName = player.getFullName();
        int[] record = (int[])headToHead.get(oppName);
        if (record == null) record = new int[] { 0, 0 };
        return name + " " + record[0] + " " + player.getName() + " " + record[1];
    }

    public String toString() {
        return name + " games: " + games + " rating: " + rating;
    }

    /**
     * Add a result to the head to head record.
     */
    public void result(PlayerWrapper opponent, boolean won) {
        games++;
        String oppName = opponent.getFullName();
        int[] record = (int[]) headToHead.get(oppName);
        if (record == null) {
            headToHead.put(oppName,won ? new int[] { 1, 0 } : new int[] { 0, 1 });
        } else {
            if (won) {
                record[0]++;
            } else {
                record[1]++;
            }
            headToHead.put(oppName,record);
        }
    }
}
