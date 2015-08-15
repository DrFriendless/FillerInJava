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

import javax.swing.table.*;

/**
 * The table model for the head to head display.
 *
 * @author John Farrell
 */
class HeadToHeadTableModel extends AbstractTableModel {
    private PlayerWrappers players;

    public HeadToHeadTableModel(PlayerWrappers players) {
        this.players = players;
    }

    public int getRowCount() {
        return players.size() + 1;
    }

    public int getColumnCount() {
        return players.size() + 1;
    }

    public Object getValueAt(int row, int column) {
        if (row == 0) {
            if (column == 0) {
                return "";
            } else {
                return players.get(column-1).getName();
            }
        } else if (column == 0) {
            return players.get(row-1).getName();
        } else {
            return players.get(row-1).getShortHeadToHead(players.get(column-1));
        }
    }
}
