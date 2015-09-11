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
import java.text.*;
import java.util.*;

/**
 * A set of players.
 * It remembers whether each player is selected or not.
 *
 * @author John Farrell
 */
public class PlayerWrappers {
    /**
     * This is the list of class names of players which are available.
     * If you create a new player, to enable them you will have to add them
     * to this list.
     */
    public static final String[] PLAYER_CLASS_NAMES = {
        "HumanFillerPlayer", "RemotePlayer", "Sachin", "Dieter", "Isadora",
        "Margaret", "Rosita", "Luigi", "Makhaya", "Claudius", "Basil", "Wanda",
        "Mainoumi", "Omar", "Shirley", "Hugo", "Eldine", "Aleksandr", "Manuelito",
        "Che", "Cochise", "Jefferson", "Chesterton", "Bronwyn", "Helen", "Blib" };
    public static final String PLAYER_PACKAGE = "friendless.games.filler.player";
    private static Random rng = new Random();

    private PlayerWrapper[] wrappers;
    private boolean[] selected;
    private ResourceBundle resources;

    /** Create a new set of players representing all of the players available. */
    public PlayerWrappers(ResourceBundle resources) {
        this.resources = resources;
        this.wrappers = getPlayerList(resources);
        selected = new boolean[wrappers.length];
        for (int i=0; i<selected.length; i++) selected[i] = true;
    }

    /**
     * Create a new set of players representing only those in the list
     * <code>players</code>.
     */
    public PlayerWrappers(ResourceBundle resources, List players) {
        this.wrappers = new PlayerWrapper[players.size()];
        wrappers = (PlayerWrapper[]) players.toArray(wrappers);
        selected = new boolean[wrappers.length];
        for (int i=0; i<selected.length; i++) selected[i] = true;
    }

    public PlayerWrappers getSelected() {
        List sel = new ArrayList(wrappers.length);
        for (int i=0; i<wrappers.length; i++) {
            if (selected[i]) sel.add(wrappers[i]);
        }
        return new PlayerWrappers(resources, sel);
    }

    public ListModel getListModel() {
        DefaultListModel model = new DefaultListModel();
        for (int i=0; i<wrappers.length; i++) {
            model.addElement(wrappers[i]);
        }
        return model;
    }

    public ComboBoxModel getComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (int i=0; i<wrappers.length; i++) {
            model.addElement(wrappers[i]);
        }
        return model;
    }

    private static PlayerWrapper[] getPlayerList(ResourceBundle resources) {
        PlayerWrapper[] wrappers = new PlayerWrapper[PLAYER_CLASS_NAMES.length];
        for (int i=0; i<wrappers.length; i++) {
            try {
                wrappers[i] = new PlayerWrapper(PLAYER_PACKAGE + "." + PLAYER_CLASS_NAMES[i], resources);
            } catch (Exception ex) {
                String format = resources.getString("filler.string.cantload");
                String mesg = MessageFormat.format(format, new Object[] { PLAYER_CLASS_NAMES[i] });
                System.out.println(mesg);
                ex.printStackTrace();
            }
            wrappers[i].setRatings();
        }
        return wrappers;
    }

    public int size() { return wrappers.length; }

    public PlayerWrapper get(int i) { return wrappers[i]; }

    /**
     * Sort the players so that highest ranking players are first.
     */
    public void sortByRatings() {
        for (int i=1; i<wrappers.length; i++) {
            PlayerWrapper pi = wrappers[i];
            for (int j=0; j<i; j++) {
                PlayerWrapper pj = wrappers[j];
                if (pi.getRating() > pj.getRating()) {
                    PlayerWrapper temp = wrappers[i];
                    wrappers[i] = wrappers[j];
                    wrappers[j] = temp;
                }
            }
        }
    }

    /**
     * "Sort" the players into a random order.
     */
    public void sortByRandom() {
        for (int i=wrappers.length-1; i>1; i--) {
            int j = rng.nextInt(i);
            PlayerWrapper pi = wrappers[i];
            wrappers[i] = wrappers[j];
            wrappers[j] = pi;
        }
    }

    public boolean contains(PlayerWrapper player) {
        if (player == null) return false;
        for (int i=0; i<wrappers.length; i++) {
            if (player.equals(wrappers[i])) {
                return true;
            }
        }
        return false;
    }

    public boolean isSelected(PlayerWrapper player) {
        if (player == null) return false;
        for (int i=0; i<wrappers.length; i++) {
            if (player.equals(wrappers[i])) {
                return selected[i];
            }
        }
        return false;
    }

    /** Change whether player <code>player</code> is selected. */
    public void setSelection(PlayerWrapper player, boolean isSelected) {
        if (player == null) return;
        for (int i=0; i<wrappers.length; i++) {
            if (player.equals(wrappers[i])) {
                selected[i] = isSelected;
            }
        }
    }

    public PlayerWrapper[] toArray() {
        PlayerWrapper[] result = wrappers.clone();
        return result;
    }

    public String toString() {
        return Arrays.asList(wrappers).toString();
    }
}
