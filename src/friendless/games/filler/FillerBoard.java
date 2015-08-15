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
import javax.swing.*;

/**
 * A graphical component which is the array of hexagons.
 *
 * @author John Farrell
 */
public class FillerBoard extends JComponent {
    /** the pixel coordinates of the hexes */
    static Point[] topLefts, botRights;

    static {
        int size = FillerSettings.SIZE;
        topLefts = new Point[size];
        botRights = new Point[size];
        for (int i=0; i<size; i++) {
            int x = FillerModel.getX(i);
            int y = FillerModel.getY(i);
            topLefts[i] = new Point(x * 5 + 5,y * 20 + (x % 2) * 10 - 5);
            botRights[i] = new Point(topLefts[i].x + 5, topLefts[i].y + 13);
        }
    }

    static Point topLeft(int i) { return topLefts[i]; }

    static Point bottomRight(int i) { return botRights[i]; }

    /** The off-screen image of the board. */
    protected Image off;
    protected FillerModel model;

    public FillerBoard() {
        this(new FillerModel());
    }

    public FillerBoard(FillerModel model) {
        this.model = model;
    }

    /**
     * Restart the board in preparation for a new game.
     * @param remoteGame True if this is a game against a remote player.
     */
    public void restart(boolean remoteGame) {
        model.randomFill(remoteGame);
        off = null;
        repaint();
    }

    public Image resetOffscreenImage() {
        Rectangle b = getBounds();
        Image img = createImage(b.width,b.height);
        Graphics goff = img.getGraphics();
        goff.setColor(getBackground());
        goff.fillRect(0,0,b.width,b.height);
        int[] pieces = model.pieces;
        int numColours = FillerSettings.colours.length;
        for (int i=0; i<pieces.length; i++) {
            if (!FillerModel.valid(i)) continue;
            // draw unknown values as shades of gray. This is only used (so far) when
            // debugging OptimalRobotPlayers.
            int ci = pieces[i];
            Color c;
            if (ci >= numColours) {
                int overflow = ci - numColours;
                int shade = (overflow * 16) % 192 + 32;
                c = new Color(shade, shade, shade);
            } else {
                c = FillerSettings.colours[ci];
            }
            drawHex(goff, c, i);
        }
        goff.dispose();
        off = img;
        return img;
    }

    public void addNotify() {
        super.addNotify();
        off = null;
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x,y,w,h);
        off = null;
    }

    /**
     * @param fast whether to try to speed things up by skipping painting.
     * Useful in robot tournaments.
     */
    public int changeColourCountScore(FillerSpace space, int newColour, int origin, boolean fast) {
        Image img = off;
        if (img == null) img = resetOffscreenImage();
        boolean[] captured = space.captured;
        Graphics goff = img.getGraphics();
        // only have to change colour of pieces already belonging to us
        int[] pieces = model.pieces;
        for (int i=0; i<captured.length; i++) {
            if (captured[i]) {
                pieces[i] = newColour;
                drawHexCentre(goff, FillerSettings.colours[newColour], i);
            }
        }
        goff.dispose();
        if (fast) {
            repaint(1000);
        } else {
            repaint();
        }
        return countScore(origin,space);
    }

    public int countScore(int origin, FillerSpace space) {
        int score = 1;
        int colour = model.pieces[origin];
        int idx = 0;
        int i, j;
        // copy pointers from space into local variables
        space.reset();
        boolean[] captured = space.captured;
        int[] counted = space.counted;
        boolean[] listed = space.listed;
        int[] border = space.border;
        // initialise arrays
        listed[0] = true;
        int[] ns = FillerModel.neighbours(origin);
        for (i=0; i<ns.length; i++) {
            int q = ns[i];
            if (q == FillerModel.NO_NEIGHBOUR) break;
            border[idx++] = q;
        }
        counted[origin] = FillerModel.MINE;
        captured[origin] = true;
        // process the positions on the border
        int[] pieces = model.pieces;
        while (idx > 0) {
            int p = border[--idx];
            if (counted[p] != FillerModel.VACANT) {
                continue;
            } else if (pieces[p] == colour) {
                counted[p] = FillerModel.MINE;
                score++;
                captured[p] = true;
                ns = FillerModel.neighbours(p);
                for (i=0; i<ns.length; i++) {
                    int q = ns[i];
                    if (q == FillerModel.NO_NEIGHBOUR) break;
                    if (!listed[q]) {
                        listed[q] = true;
                        border[idx++] = q;
                    }
                }
            } else {
                counted[p] = FillerModel.BORDER;
            }
        }
        return score;
    }

    /**
     * Fill in the coloured part of a hex. If it is one of the origins, draw the little
     * letter over the top.
     */
    void drawHexCentre(Graphics g, Color c, int p) {
        Point n = topLeft(p);
        int x = n.x;
        int y = n.y;
        g.setColor(c);
        int l, t, r, b;
        for (int i=0; i<4; i++) {
            l = x-i;
            t = y+i+1;
            r = x+1+i;
            b = y+12-i;
            g.drawLine(l,t,l,b);
            g.drawLine(r,t,r,b);
        }
        if (p == FillerSettings.ORIGINS[0]) {
            drawLeft(g,contrastingColour(c),topLeft(FillerSettings.ORIGINS[0]));
        } else if (p == FillerSettings.ORIGINS[1]) {
            drawRight(g,contrastingColour(c),topLeft(FillerSettings.ORIGINS[1]));
        }
    }

    /** @return a colour which can be read clearly when drawn on top of <code>c</code>. */
    static Color contrastingColour(Color c) {
        if (c.equals(Color.black) || c.equals(Color.blue) || c.equals(Color.darkGray)) {
            return Color.white;
        } else {
            return Color.black;
        }
    }

    /** Draw the letter 'R' at the physical coordinate <code>n</code>. */
    void drawRight(Graphics g, Color c, Point n) {
        int x = n.x;
        int y = n.y;
        g.setColor(c);
        // teensy weensy 'R'
        g.drawLine(x-1,y+4,x-1,y+9);
        g.drawLine(x-1,y+4,x+1,y+4);
        g.drawLine(x+2,y+5,x+2,y+6);
        g.drawLine(x-1,y+7,x+1,y+7);
        g.drawLine(x+1,y+8,x+2,y+9);
    }

    /** Draw the letter 'L' at the physical coordinate <code>n</code>. */
    void drawLeft(Graphics g, Color c, Point n) {
        int x = n.x;
        int y = n.y;
        g.setColor(c);
        // teensy weensy 'L'
        g.drawLine(x-1,y+4,x-1,y+9);
        g.drawLine(x-1,y+9,x+2,y+9);
    }

    /**
     * Draw hex number <code>i</code> in colour <code>c</code>.
     * This method draws the outline and lets drawHexCentre fill in the
     * coloured part.
     */
    void drawHex(Graphics g, Color c, int i) {
        Point n = topLeft(i);
        int x = n.x;
        int y = n.y;
        g.setColor(Color.white);
        g.drawLine(x,y,x-4,y+4);
        g.drawLine(x-4,y+4,x-4,y+9);
        g.drawLine(x-4,y+9,x,y+13);
        g.setColor(Color.darkGray);
        g.drawLine(x+1,y,x+5,y+4);
        g.drawLine(x+5,y+4,x+5,y+9);
        g.drawLine(x+5,y+9,x+1,y+13);
        drawHexCentre(g,c,i);
    }

    public Dimension getMinimumSize() { return getPreferredSize(); }

    public Dimension getPreferredSize() {
        Point p1 = bottomRight(FillerModel.makeIndex(FillerSettings.COLUMNS-1,FillerSettings.ROWS-1));
        Point p2 = bottomRight(FillerModel.makeIndex(FillerSettings.COLUMNS-2,FillerSettings.ROWS-1));
        Dimension dim = new Dimension((p1.x < p2.x) ? p2.x : p1.x, (p1.y < p2.y) ? p2.y : p1.y);
        dim.width += 5;
        dim.height += 5;
        return dim;
    }

    public void paintComponent(Graphics g) {
        if (off == null) resetOffscreenImage();
        g.drawImage(off,0,0,this);
    }
}
