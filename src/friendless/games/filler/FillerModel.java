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

import java.io.IOException;
import java.util.*;

import friendless.games.filler.remote.RemoteConnection;
import friendless.games.filler.remote.IsMessage;
import friendless.games.filler.remote.IsMessageID;
import friendless.games.filler.remote.messages.NewGameMessage;

/**
 * A class which represents what colour the hexagons are.
 * This class has been extended to cater for the needs of robot players.
 *
 * @author John Farrell
 */
public class FillerModel {
    /* Constants used in calculations. */
    /** We have not looked at this piece yet, we don't know. */
    public static final int VACANT = 0;
    /** It's on his border, and I could get it. */
    public static final int HIS_BORDER = 1;
    /** It's on both of our borders. */
    public static final int SHARED_BORDER = 2;
    /** He owns it. */
    public static final int HIS = 3;
    /** I do not own it, but only I can ever own it. */
    public static final int REACHABLE = 4;
    /** He does not own it, but only he can ever own it. */
    public static final int HIS_REACHABLE = 5;
    /** It's on my border, and he could get it. */
    public static final int BORDER = 6;
    /** I own it. */
    public static final int MINE = 7;
    /** Either of us can get to this piece and it's not on our borders. */
    public static final int FREE = 8;
    /** It's on my border, and he can't get it. */
    public static final int INTERNAL_BORDER = 9;
    /** It's on his border, and I can't get it. */
    public static final int HIS_INTERNAL_BORDER = 10;
    /** Number of types things can be classified to be. */
    public static final int NUM_TYPES = 11;
    /** Distance to cells we already own. */
    public static final int NO_DISTANCE = 0;
    /** Distance to unreachable cells. */
    public static final int UNREACHABLE_DISTANCE = -1;
    /** Uncalculated distance. */
    public static final int UNKNOWN_DISTANCE = -2;
    /** Maximum neighbours that a cell could have. */
    private static final int MAX_NEIGHBOURS = 6;
    /** Indicator that a cell has no neighbour. */
    public static final int NO_NEIGHBOUR = -1;
    private static int count = 0;
    private static int[] x, y;
    private static int[][] neighs;
    private static final Random rng = new Random();
    public static final BitSet MUST_BE_MINE = new BitSet(NUM_TYPES);
    public static final BitSet MUST_BE_HIS = new BitSet(NUM_TYPES);
    public static final BitSet MUST_BE_FREE = new BitSet(NUM_TYPES);

    static {
        MUST_BE_FREE.set(HIS_BORDER);
        MUST_BE_FREE.set(SHARED_BORDER);
        MUST_BE_HIS.set(HIS);
        MUST_BE_MINE.set(REACHABLE);
        MUST_BE_HIS.set(HIS_REACHABLE);
        MUST_BE_FREE.set(BORDER);
        MUST_BE_MINE.set(MINE);
        MUST_BE_FREE.set(FREE);
        MUST_BE_MINE.set(INTERNAL_BORDER);
        MUST_BE_HIS.set(HIS_INTERNAL_BORDER);
        final int size = FillerSettings.SIZE;
        final int rows = FillerSettings.ROWS;
        x = new int[size];
        y = new int[size];
        for (int i=0; i<size; i++) {
            if (((i/rows)%2 == 0) && (i%rows == 0)) {
                x[i] = -1;
                y[i] = -1;
            } else {
                x[i] = i/rows;
                y[i] = i%rows;
            }
        }
        //
        neighs = new int[size][MAX_NEIGHBOURS];
        for (int i=0; i<size; i++) {
            if (x[i] >= 0) calcNeighbours(i, neighs[i]);
        }
    }

    /**
     * Calculate what the neighbours of cell <code>i</code> are.
     * Put them into the first elements of the array <code>ps</code>.
     * Fill the remaining spaces with the value <code>NO_NEIGHBOUR</code>.
     */
    private static final int[] calcNeighbours(int i, int[] ps) {
        final int columns = FillerSettings.COLUMNS;
        final int rows = FillerSettings.ROWS;
        int xi = x[i];
        int yi = y[i];
        int idx = 0;
        // to the right
        if (xi < columns-2) ps[idx++] = makeIndex(xi+2, yi);
        // to the left
        if (xi > 1) ps[idx++] = makeIndex(xi-2, yi);
        if (xi % 2 == 0) {
            if (xi > 0) {
                // above and below to the left
                ps[idx++] = makeIndex(xi-1, yi-1);
                ps[idx++] = makeIndex(xi-1, yi);
            }
            if (xi < columns-1) {
                // above and below to the right
                ps[idx++] = makeIndex(xi+1, yi-1);
                ps[idx++] = makeIndex(xi+1, yi);
            }
        } else {
            if (yi > 0) {
                // two above
                ps[idx++] = makeIndex(xi-1, yi);
                ps[idx++] = makeIndex(xi+1, yi);
            }
            if (yi < rows-1) {
                // two below
                ps[idx++] = makeIndex(xi-1, yi+1);
                ps[idx++] = makeIndex(xi+1, yi+1);
            }
        }
        while (idx < ps.length) ps[idx++] = NO_NEIGHBOUR;
        return ps;
    }

    public static final int makeIndex(int x, int y) { return (int)(x * FillerSettings.ROWS + y); }

    public static final int getX(int i) { return x[i]; }

    public static final int getY(int i) { return y[i]; }

    public static final boolean valid(int i) { return (x[i] >= 0); }

    public static final int[] neighbours(int i) { return neighs[i]; }

    public static final boolean isPerimeter(int i) {
        int[] ns = neighs[i];
        return ns[ns.length-1] == NO_NEIGHBOUR;
    }

    /**
     * Update the <code>reachable</code> array to determine whether each
     * piece can ever be reached by me.
     */
    static void allocateFree(FillerModel model, FillerPlayerSpace space, int myBorder,
            boolean[] reachable) {
        //int hisBorder = BORDER + HIS_BORDER - myBorder;
        space.resetListed();
        int[] counted = space.counted;
        boolean[] listed = space.listed;
        int[] border = space.border;
        // build up the list of positions on the border
        int idx = 0;
        for (int i=0; i<FillerSettings.SIZE; i++) {
            if ((counted[i] == myBorder) || (counted[i] == SHARED_BORDER)) {
                listed[i] = true;
                border[idx++] = i;
            }
        }
        // process the positions on the border
        int[] ns = null;
        while (idx > 0) {
            // take a point which is currently on the border
            int p = border[--idx];
            switch (counted[p]) {
                case VACANT:
                case BORDER:
                case SHARED_BORDER:
                case HIS_BORDER:
                    reachable[p] = true;
                    ns = neighs[p];
                    for (int i=0; i<ns.length; i++) {
                        int q = ns[i];
                        if (q == NO_NEIGHBOUR) break;
                        if (!listed[q]) {
                            listed[q] = true;
                            border[idx++] = q;
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Assuming that I am the player who started at <code>origin</code>,
     * fill in details in <code>space</code> to mark where my pieces and my
     * border are. As this filling may have already been done for the other player,
     * it's possible that locations on my border may already be marked as being
     * on his border. In those cases, change them to SHARED_BORDER.
     */
    protected static void allocate(FillerModel model, int origin, FillerPlayerSpace space,
            int mine, int myBorder) {
        int hisBorder = BORDER + HIS_BORDER - myBorder;
        int his = MINE + HIS - mine;
        space.resetListed();
        int[] counted = space.counted;
        boolean[] listed = space.listed;
        int[] border = space.border;
        int colour = model.pieces[origin];
        // origin is mine, its neighbours are the original border
        counted[origin] = mine;
        listed[origin] = true;
        int idx = 0;
        int[] ns = neighs[origin];
        for (int i=0; i<ns.length; i++) {
            int q = ns[i];
            if (q == NO_NEIGHBOUR) break;
            listed[q] = true;
            border[idx++] = q;
        }
        // process the positions on the border
        while (idx > 0) {
            // take a point which is currently on the border
            int p = border[--idx];
            int thisPiece = counted[p];
            if ((thisPiece == VACANT) || (thisPiece == hisBorder)) {
                if (model.pieces[p] == colour) {
                    // on the border and my colour must be mine
                    counted[p] = mine;
                    ns = neighs[p];
                    for (int i=0; i<ns.length; i++) {
                        int q = ns[i];
                        if (q == NO_NEIGHBOUR) break;
                        if (!listed[q]) {
                            listed[q] = true;
                            border[idx++] = q;
                        }
                    }
                } else if (thisPiece == hisBorder) {
                    // on my border and his border must be shared border
                    counted[p] = SHARED_BORDER;
                } else {
                    // on the border and another colour but not his border must be my border
                    counted[p] = myBorder;
                    ns = neighs[p];
                    for (int i=0; i<MAX_NEIGHBOURS; i++) {
                        int q = ns[i];
                        if (q == NO_NEIGHBOUR) break;
                        if (!listed[q] && (model.pieces[p] == model.pieces[q]) && (counted[q] != his)) {
                            listed[q] = true;
                            border[idx++] = q;
                        }
                    }
                }
            }
        }
    }

    /**
     * Build a calculation of who owns what and who has what influence over what.
     */
    static void allocateTypes(FillerModel model, int[] origins, FillerPlayerSpace space) {
        // copy pointers from space into local variables
        int[] counted = space.counted;
        boolean[] reachable = space.reachable;
        boolean[] hisReachable = space.hisReachable;
        space.reset();
        space.resetReachable();
        allocate(model, origins[0], space, MINE, BORDER);
        allocate(model, origins[1], space, HIS, HIS_BORDER);
        allocateFree(model, space, BORDER, reachable);
        allocateFree(model, space, HIS_BORDER, hisReachable);
        for (int i=0; i<reachable.length; i++) {
            if (reachable[i]) {
                if (!hisReachable[i]) {
                    if (counted[i] == BORDER) {
                        counted[i] = INTERNAL_BORDER;
                    } else {
                        counted[i] = REACHABLE;
                    }
                } else if (counted[i] == VACANT) {
                    counted[i] = FREE;
                }
            } else if (hisReachable[i]) {
                if (counted[i] == HIS_BORDER) {
                    counted[i] = HIS_INTERNAL_BORDER;
                } else {
                    counted[i] = HIS_REACHABLE;
                }
            }
        }
    }

    /**
     * Assumes that allocateTypes has already been called with this model and
     * this space, so the information in <code>space</code> accurately reflects
     * the state of the <code>model</code>.
     */
    static void allocateDistance(FillerModel model, FillerPlayerSpace space) {
        int[] counted = space.counted;
        int[] distance = space.distance;
        int[] border = space.border;
        int[] pieces = model.pieces;
        space.resetListed();
        boolean[] listed = space.listed;
        int idx = 0;
        // allocate the distances we know immediately
        for (int i=0; i<distance.length; i++) {
            int ci = counted[i];
            if (!valid(i)) {
                distance[i] = UNREACHABLE_DISTANCE;
            } else if (MUST_BE_MINE.get(ci)) {
                distance[i] = NO_DISTANCE;
            } else if (MUST_BE_HIS.get(ci)) {
                distance[i] = UNREACHABLE_DISTANCE;
            } else if (ci == BORDER || ci == SHARED_BORDER) {
                distance[i] = 1;
                // remember where the border is
                border[idx++] = i;
                listed[i] = true;
            } else {
                distance[i] = UNKNOWN_DISTANCE;
            }
        }
        // figure out the unknown distances
        while (idx > 0) {
            // take a point which is currently on the border
            int p = border[--idx];
            listed[p] = false;
            int distp = distance[p];
            int[] ns = neighs[p];
            for (int i=0; i<ns.length; i++) {
                int q = ns[i];
                if (q == NO_NEIGHBOUR) break;
                int distq = distance[q];
                if (distq == UNREACHABLE_DISTANCE || distq == NO_DISTANCE) {
                    continue;
                }
                // expected distance to q
                int exdistq = (pieces[q] == pieces[p]) ? distp : (distp + 1);
                if (distq == UNKNOWN_DISTANCE || exdistq < distq) {
                    // found a new or shorter route to q
                    distance[q] = exdistq;
                    if (!listed[q]) {
                        border[idx++] = q;
                        listed[q] = true;
                    }
                }
            }
        }
    }

    int[] pieces;

    public FillerModel() {
        this.pieces = new int[FillerSettings.SIZE];
    }

    public FillerModel(int[] pieces) {
        if (pieces.length != FillerSettings.SIZE) {
            throw new IllegalArgumentException("pieces wrong length");
        }
        this.pieces = (int[]) pieces.clone();
    }

    /**
     * @param remoteGame True if this is a game against a remote player.
     */
    void randomFill(boolean remoteGame) {
        if (remoteGame && !RemoteConnection.getInstance().isServer()) {
            // This is the client side of a game against a remote player.
            // We will wait for the NewGame message which carries the
            // board layout.
            try {
                IsMessage msg =
                    RemoteConnection.getInstance().receiveMessage();
                if (msg.getMessageId() == IsMessageID.MSGID_GAME_NEW) {
                    setPieces(((NewGameMessage) msg).getPieces());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            // This is not a remote game or we are the server side of
            // a remote game.
            for (int i=0; i<pieces.length; i++) {
                pieces[i] = (valid(i)) ? rng.nextInt(FillerSettings.NUM_COLOURS) : -1;
            }
            if (remoteGame) {
                try {
                    NewGameMessage msg = new NewGameMessage(pieces);
                    RemoteConnection.getInstance().sendMessage(msg);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public int[] getPieces() {
        return pieces;
    }

    public void setPieces(int[] pieces) {
        if (pieces.length != FillerSettings.SIZE) {
            throw new IllegalArgumentException("pieces wrong length");
        }
        this.pieces = (int[]) pieces.clone();
    }
}
