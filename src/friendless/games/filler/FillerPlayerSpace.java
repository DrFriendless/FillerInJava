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
 * Memory that a Filler robot player uses to do calculations.
 * This exists so that no other temporary space needs to be allocated during
 * calculations.
 * We do this to avoid garbage collections.
 *
 * @author John Farrell
 */
public class FillerPlayerSpace {
    private static final boolean[] UNLISTED;
    private static final int[] UNCOUNTED;
    /** True iff the piece is already considered or is listed to be considered. */
    public boolean[] listed;
    /** the data for the model we build up during calculations */
    public int[] counted;
    /** Space for keeping a stack of border positions in */
    public int[] border;
    /** Whether I can reach each place. */
    public boolean[] reachable;
    /** Whether he can reach each place. */
    public boolean[] hisReachable;
    /** Minimum number of turns from our territory to free spaces. */
    public int[] distance;
    /** Minimum number of turns opponent's territory to free spaces. */
    public int[] opponentDistance;

    static {
        UNLISTED = new boolean[FillerSettings.SIZE];
        UNCOUNTED = new int[FillerSettings.SIZE];
        for (int i=0; i<UNCOUNTED.length; i++) {
            UNCOUNTED[i] = FillerModel.VACANT;
        }
    }

    public FillerPlayerSpace() {
        int size = FillerSettings.SIZE;
        reachable = new boolean[size];
        hisReachable = new boolean[size];
        counted = new int[size];
        distance = new int[size];
        opponentDistance = new int[size];
        listed = new boolean[size];
        border = new int[size];
    }

    public void resetReachable() {
        System.arraycopy(UNLISTED, 0, reachable, 0, reachable.length);
        System.arraycopy(UNLISTED, 0, hisReachable, 0, hisReachable.length);
    }

    public void resetListed() {
        System.arraycopy(UNLISTED, 0, listed, 0, listed.length);
    }

    public void resetCounted() {
        System.arraycopy(UNCOUNTED, 0, counted, 0, counted.length);
        System.arraycopy(UNCOUNTED, 0, distance, 0, distance.length);
        System.arraycopy(UNCOUNTED, 0, opponentDistance, 0, opponentDistance.length);
    }

    public void reset() {
        resetListed();
        resetCounted();
        // don't clear border, you don't need to
    }
}
