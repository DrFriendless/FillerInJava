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
import java.util.List;

/**
 * Miscellaneous configuration settings for the game.
 * These are values which could conceivably change in the future.
 *
 * @author John Farrell
 */
public class FillerSettings {
    public static final Color[] colours = { Color.red, Color.green, Color.yellow, Color.blue,
                                            Color.magenta, Color.darkGray, Color.cyan, Color.white,
                                            Color.pink, new Color(0,127,0), new Color(0,0,127) };
    public static final int[] ORIGINS = { FillerModel.makeIndex(1,14), FillerModel.makeIndex(93,0) };
    public static final int NUM_COLOURS = 9;
    /**
     * These colour names are used in debugging, amd are not seen by the player,
     * so they don't need to be internationalised.
     */
    public static final String[] colourNames = { "red", "green", "yellow", "blue",
        "magenta", "gray", "cyan", "white", "pink" };
    public static final int COLUMNS = 95;
    public static final int ROWS = 15;
    public static final int SIZE = COLUMNS * ROWS;
    public static final int POINTS_TO_WIN = 689;

    /** You don't need one of these. */
    private FillerSettings() { }

    public static List colourSetToList(BitSet colours) {
        List l = new ArrayList(colours.size());
        for (int i=0; i<NUM_COLOURS; i++) {
            if (colours.get(i)) {
                l.add(colourNames[i]);
            }
        }
        return l;
    }
}
