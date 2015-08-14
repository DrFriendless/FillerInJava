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
import javax.swing.border.*;
import javax.swing.table.*;
import friendless.games.filler.player.*;

/**
 * A class to render a player in a list or a combo box.
 * Draws the player's name and an icon.
 *
 * @author John Farrell
 */
public final class PlayerRenderer implements ListCellRenderer, TableCellRenderer {
    /** Icon for interactive players. */
    static final String DEFAULT_HUMAN = "defaultHuman.gif";
    /** Icon for robot players. */
    static final String DEFAULT_ALIEN = "defaultAlien.gif";
    /** All the icons the renderer might need. */
    static Map icons = new HashMap();
    static {
        loadImage(DEFAULT_HUMAN);
        loadImage(DEFAULT_ALIEN);
    }
    static final EmptyBorder border = new EmptyBorder(2,2,2,2);
    protected Color fg, bg;
    protected JLabel l = new JLabel();

    public PlayerRenderer(Color fg, Color bg) {
        this.fg = fg;
        this.bg = bg;
    }

    private JLabel getLabel(ImageIcon icon, String value, boolean selected) {
        l.setText(value == null  ? "Unnamed" : value);
        l.setIcon(icon);
        l.setOpaque(true);
        l.setBackground(selected ? fg : bg);
        l.setForeground(selected ? bg : fg);
        l.setBorder(border);
        l.setHorizontalTextPosition(SwingConstants.RIGHT);
        l.setIconTextGap(8);
        return l;
    }

    private ImageIcon getIcon(PlayerWrapper player) {
        String imgFile = player.getIcon();
        if (imgFile != null) {
            loadImage(imgFile);
            ImageIcon icon =(ImageIcon) icons.get(imgFile);
            if (icon != null) return icon;
        }
        if (player.getPlayerClass().equals(HumanFillerPlayer.class)) {
            return (ImageIcon) icons.get(DEFAULT_HUMAN);
        } else {
            return (ImageIcon) icons.get(DEFAULT_ALIEN);
        }
    }

    static void loadImage(String imgFile) {
        if (icons.get(imgFile) != null) return;
        try {
            java.net.URL url = PlayerRenderer.class.getResource(imgFile);
            icons.put(imgFile,new ImageIcon(url));
        } catch (Exception ex) {
        }
    }

    public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        PlayerWrapper player = (PlayerWrapper)value;
        return getLabel(getIcon(player), player.getName(),isSelected);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        PlayerWrapper player = (PlayerWrapper)value;
        return getLabel(getIcon(player), player.getName(),isSelected);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        PlayerWrapper player = (PlayerWrapper)value;
        return getLabel(getIcon(player), player.getName(),isSelected);
    }
}
