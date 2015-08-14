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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import friendless.awt.*;
import friendless.games.filler.player.*;

/**
 * A panel that displays the historical head to head results.
 *
 * @author John Farrell
 */
class HeadToHeadPanel extends JPanel {
    JTable table;
    PlayerWrappers players;

    public HeadToHeadPanel(PlayerWrappers players) {
        this.players = players;
        setLayout(new BorderLayout(4,4));
        add("Center", table = new JTable());
    }

    public void refresh() {
        players.sortByRatings();
        table.setModel(new HeadToHeadTableModel(players));
    }
}

