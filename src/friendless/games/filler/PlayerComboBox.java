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
import java.util.*;
import javax.swing.*;

/**
 * A combo box from which you can choose a player.
 *
 * @author John Farrell
 */
class PlayerComboBox extends JComboBox {
    public PlayerComboBox(PlayerWrappers players, ResourceBundle resources) {
        super(players.getComboBoxModel());
        setRenderer(new PlayerRenderer(Color.black, Color.white));
        setSelectedIndex(0);
        setEditable(false);
        getAccessibleContext().setAccessibleName(resources.getString("filler.label.players"));
        getAccessibleContext().setAccessibleDescription(resources.getString("filler.string.choose"));
    }
}
