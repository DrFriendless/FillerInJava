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

import javax.swing.*;
import javax.swing.event.*;

/**
 * A list of all players, from which you can choose a subset.
 *
 * @author John Farrell
 */
public final class ChoosePlayerList extends JList {
    PlayerRenderer renderer;
    PlayerWrappers players;

    public ChoosePlayerList(PlayerWrappers players, int selectionModel) {
        super();
        this.players = players;
        renderer = new PlayerRenderer(getForeground(), getBackground());
        setCellRenderer(renderer);
        setSelectionMode(selectionModel);
        setModel(players.getListModel());
        if (selectionModel == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
            setSelected();
            addListSelectionListener(new ChoosePlayersListSelectionListener(players));
        }
    }

    void setSelected() {
        PlayerWrappers selected = players.getSelected();
        for (int i=0; i<players.size(); i++) {
            if (selected.contains(players.get(i))) {
                getSelectionModel().addSelectionInterval(i, i);
            }
        }
    }

    class ChoosePlayersListSelectionListener implements ListSelectionListener {
        PlayerWrappers players;

        public ChoosePlayersListSelectionListener(PlayerWrappers players) {
            this.players = players;
        }

        public void valueChanged(ListSelectionEvent e) {
            sync(((JList) e.getSource()).getSelectionModel());
        }

        /**
         * Update the selection in the player list to match that of the ListSelectionModel.
         * This code is really bad.
         */
        void sync(ListSelectionModel model) {
            for (int i=0; i<players.size(); i++) {
                players.setSelection((PlayerWrapper) getModel().getElementAt(i), model.isSelectedIndex(i));
            }
        }
    }
}
