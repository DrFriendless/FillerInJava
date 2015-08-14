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

/**
 * A base class for implementing robot players, which provides some useful
 * operations, but no strategy.
 *
 * @author John Farrell
 */
public abstract class AbstractFillerPlayer implements FillerPlayer {
    /** Get the the X coordinate of piece <code>i</code>. */
    protected static final int getX(int i) { return FillerModel.getX(i); }

    /** Get the the Y coordinate of piece <code>i</code>. */
    protected static final int getY(int i) { return FillerModel.getY(i); }

    /** Make a piece index from a given x and y. */
    protected static final int makeIndex(int x, int y) {
        return FillerModel.makeIndex(x, y);
    }

    protected static Random rng = new Random();

    /** origins[0] is this player's origin, origins[1] is the opponents. */
    protected int[] origins;
    /** same as origins but with the order swapped. */
    protected int[] reverseOrigins;

    public void setOrigin(int origin, int otherOrigin) {
        origins = new int[] { origin, otherOrigin };
        reverseOrigins = new int[] { otherOrigin, origin };
    }

   /** To be overridden by each player. */
    public abstract int turn();

    protected static BitSet allColours() {
        BitSet b = new BitSet(FillerSettings.NUM_COLOURS);
        for (int i=0; i<FillerSettings.NUM_COLOURS; i++) b.set(i);
        return b;
    }

    protected final int[] copy(int[] src) {
        return (int[]) src.clone();
    }

    protected int chooseRandom(BitSet choices) {
        if (choices == null) return -1;
        int count = 0;
        for (int i=0; i<FillerSettings.NUM_COLOURS; i++) {
            if (choices.get(i)) count++;
        }
        if (count == 0) return -1;
        int r = rng.nextInt(count);
        count = 0;
        for (int i=0; i<FillerSettings.NUM_COLOURS; i++) {
            if (choices.get(i)) {
                if (count == r) return i;
                count++;
            }
        }
        return -1;
    }

    static final int[] ROOTS = { 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4 };

    protected static final int intRoot(int x) {
        if (x < 20) return ROOTS[x];
        int a1 = x/2;
        int a2 = x/a1;
        while (Math.abs(a1-a2) > 1) {
            a1 = (a1 + a2)/2;
            a2 = x/a1;
        }
        return a1;
    }

    protected static final int sideDistance(int p1, int p2) {
        return Math.abs(getX(p1)-getX(p2)) + Math.abs(getY(p1)-getY(p2));
    }

    protected static final int diagDistance(int p1, int p2) {
        int x = getX(p1)-getX(p2);
        int y = (getY(p1)-getY(p2)) * 3;
        return intRoot(x*x + y*y);
    }

    public String getIcon() { return null; }
}
