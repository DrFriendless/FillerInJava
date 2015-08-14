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

/**
 * Space that the game needs to do its calculations.
 * This is used as a source of temporary memory, to avoid allocating and
 * reallocating the space all the time.
 * We do this for efficiency.
 *
 * @author John Farrell
 */
public class FillerSpace {
    public boolean[] captured;
    public boolean[] listed;
    public int[] counted;
    public int[] border;
    private static final boolean[] UNLISTED;
    private static final int[] UNCOUNTED;

    static {
        UNLISTED = new boolean[FillerSettings.SIZE];
        UNCOUNTED = new int[FillerSettings.SIZE];
        for (int i=0; i<UNCOUNTED.length; i++) {
            UNCOUNTED[i] = FillerModel.VACANT;
        }
    }

    public FillerSpace() {
        captured = new boolean[FillerSettings.SIZE];
        counted = new int[FillerSettings.SIZE];
        listed = new boolean[FillerSettings.SIZE];
        border = new int[FillerSettings.SIZE];
    }

    public void resetListed() {
        System.arraycopy(UNLISTED, 0, listed, 0, listed.length);
    }

    public void resetCounted() {
        System.arraycopy(UNCOUNTED, 0, counted, 0, counted.length);
    }

    public void reset() {
        resetListed();
        resetCounted();
        // don't touch captured, it needs to be preserved
        // don't clear border, you don't need to
    }
}
