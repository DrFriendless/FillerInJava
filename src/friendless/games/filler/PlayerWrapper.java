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

import java.util.*;
import javax.swing.*;

/**
 * A wrapper around a player class to provide some player-related functionality,
 * such as the name of the player if it is distinct from the class.
 * This will (eventually) be the case for human players.
 *
 * @author John Farrell
 */
class PlayerWrapper {
    Class fpClass;
    boolean enabled;
    String name, className;
    PlayerRating rating;
    ResourceBundle resources;
    String icon;

    public PlayerWrapper(String className, ResourceBundle resources) {
        this.resources = resources;
        this.enabled = true;
        try {
            this.className = className;
            this.fpClass = Class.forName(className);
        } catch (ClassNotFoundException cnfx) {
            cnfx.printStackTrace();
            System.exit(1);
        }
    }

    public void setRatings() {
        rating = PlayerRatings.includePlayer(this);
    }

    /** Whether this player is enabled. */
    public boolean isEnabled() { return enabled; }

    /** Set whether this player is enabled. */
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int hashCode() { return getFullName().hashCode(); }

    public boolean equals(Object o) {
        if (!(o instanceof PlayerWrapper)) return false;
        PlayerWrapper pw = (PlayerWrapper) o;
        return getFullName().equals(pw.getFullName());
    }

    public String getFullName() {
        if (name == null) getDetailsFromClass();
        return className + "/" + name;
    }

    public void setName(String name) { this.name = name; }

    void getDetailsFromClass() {
        try {
            FillerPlayer player = getInstance();
            name = player.getName();
            icon = player.getIcon();
        } catch (Exception ex) {
        }
    }

    public String getName() {
        if (name == null) getDetailsFromClass();
        try {
            return resources.getString("player.name." + name);
        } catch (MissingResourceException ex) {
            ex.printStackTrace();
            return name;
        }
    }

    public String getIcon() {
        if (icon == null) getDetailsFromClass();
        return icon;
    }

    Class getPlayerClass() { return fpClass; }

    /** Get a FillerPlayer from this class. */
    FillerPlayer getInstance() {
        try {
            return (FillerPlayer)fpClass.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**
     * @return the head to head record of this player against <code>opponent</code>.
     * The array has two values, the first is the number of games won by this player
     * and the second is the number won by the opponent.
     */
    int[] getRecordAgainst(PlayerWrapper opponent) {
        return rating.getRecordAgainst(opponent);
    }

    String getShortHeadToHead(PlayerWrapper opponent) {
        return rating.getShortHeadToHead(opponent);
    }

    int getRating() { return rating.rating; }

    public String toString() { return getName() + " (" + getRating() + ")"; }

    public String getDescription() {
        if (name == null) getDetailsFromClass();
        try {
            return resources.getString("description." + name);
        } catch (MissingResourceException ex) {
            ex.printStackTrace();
            return "{0}";
        }
    }
}
