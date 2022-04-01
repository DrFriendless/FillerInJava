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
import java.awt.image.BufferedImage;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import javax.imageio.ImageIO;
import javax.swing.*;


/**
 * A graphical component which is the array of hexagons.
 *
 * @author John Farrell
 */
public class FillerBoard extends JComponent implements ComponentListener {
    /** the pixel coordinates of the hexes */
    Point[] topLefts, botRights;
    public final static int MIN_SIZE = 5;
    int count;
    int SIZE = MIN_SIZE;
    Dimension dim;

    public void updateSize() {
        Dimension mySize = getSize();
        int dX = FillerSettings.COLUMNS > 0 ? mySize.width / (FillerSettings.COLUMNS + 1) : MIN_SIZE;
        int dY = FillerSettings.ROWS > 0 ? mySize.height / FillerSettings.ROWS  / 4 : MIN_SIZE;
//        System.out.printf("width=%d height=%d cols=%d rows=%d dx=%d dy=%d\n",
//            mySize.width, mySize.height, FillerSettings.COLUMNS, FillerSettings.ROWS, dX, dY);
        SIZE = (dX < dY) ? dX : dY;
        if (SIZE < MIN_SIZE) { SIZE = MIN_SIZE; }
        int size = FillerSettings.SIZE;
        for (int i=0; i<count; i++) {
            int x = FillerModel.getX(i);
            int y = FillerModel.getY(i);
            topLefts[i] = new Point(x * SIZE + SIZE,y * SIZE * 4 + (x % 2) * SIZE * 2 - SIZE);
            botRights[i] = new Point(topLefts[i].x + SIZE, topLefts[i].y + SIZE + SIZE + (SIZE+1)/2);
        }
    }

    public void componentHidden(ComponentEvent e) { /* System.out.printf("componentHidden\n"); */ }
    public void componentMoved(ComponentEvent e) { /* System.out.printf("componentMoved\n"); */ }
    public void componentShown(ComponentEvent e) { /* System.out.printf("componentShown\n"); */ }
    public void componentResized(ComponentEvent e) {
        /* System.out.printf("componentResized\n"); */
        updateSize();
        revalidate();
        repaint();
    }

    Point topLeft(int i) { return topLefts[i]; }

    Point bottomRight(int i) { return botRights[i]; }

    /** The off-screen image of the board. */
    protected BufferedImage off;
    protected FillerModel model;

    public FillerBoard() {
        this(new FillerModel());
        count = FillerSettings.SIZE;
        topLefts = new Point[count];
        botRights = new Point[count];
        dim = new Dimension(600, 400);
        this.addComponentListener(this);
        updateSize();
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

    private static int imageFrame = 0;

    public BufferedImage resetOffscreenImage() {
        Rectangle b = getBounds();
        BufferedImage img = new BufferedImage(b.width, b.height, BufferedImage.TYPE_INT_RGB);
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
            } else if (ci < 0) {
                c = Color.WHITE;
            } else {
                c = FillerSettings.colours[ci];
            }
            drawHex(goff, c, i);
        }
        goff.dispose();
        writeImageFile(img);
        off = img;
        return img;
    }

    private void writeImageFile(BufferedImage img) {
//        String filename = MessageFormat.format("frames/game_{0,number,000}.png", imageFrame++);
//        try {
//            File f = new File(filename);
//            ImageIO.write(img, "png", f);
//        } catch (IOException ex) {
//        }
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
        BufferedImage img = off;
        if (img == null) img = resetOffscreenImage();
        boolean[] captured = space.captured;
        Graphics goff = img.getGraphics();
        // only have to change myColour of pieces already belonging to us
        int[] pieces = model.pieces;
        for (int i=0; i<captured.length; i++) {
            if (captured[i]) {
                pieces[i] = newColour;
                drawHexCentre(goff, FillerSettings.colours[newColour], i);
            }
        }
        goff.dispose();
        writeImageFile(img);
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
        for (int q : ns) {
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
                for (int q : ns) {
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
    private void drawHexCentre(Graphics g, Color c, int p) {
        Point n = topLeft(p);
        int x = n.x;
        int y = n.y;
        g.setColor(c);
        int l, t, r, b;
        for (int i=0; i<SIZE-1; i++) {
            l = x-i;
            t = y+i+1;
            r = x+1+i;
            b = y+SIZE*5/2-i;
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
    private void drawRight(Graphics g, Color c, Point n) {
        int x = n.x;
        int y = n.y + SIZE - 5;
        g.setColor(c);
        // teensy weensy 'R'
        g.drawLine(x-1,y+4,x-1,y+9);
        g.drawLine(x-1,y+4,x+1,y+4);
        g.drawLine(x+2,y+5,x+2,y+6);
        g.drawLine(x-1,y+7,x+1,y+7);
        g.drawLine(x+1,y+8,x+2,y+9);
    }

    /** Draw the letter 'L' at the physical coordinate <code>n</code>. */
    private void drawLeft(Graphics g, Color c, Point n) {
        int x = n.x;
        int y = n.y + SIZE - 5;
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
    protected void drawHex(Graphics g, Color c, int i) {
        Point n = topLeft(i);
        int x = n.x;
        int y = n.y;
        g.setColor(Color.white);
        int size1 = SIZE - 1;
        int size21 = SIZE + size1;
        int size31 = SIZE + size1 + size1;
        g.drawLine(x,y,x-size1,y+size1);
        g.drawLine(x-size1,y+size1,x-size1,y+size21);
        g.drawLine(x-size1,y+size21,x,y+size31);
        g.setColor(Color.darkGray);
        g.drawLine(x+1,y,x+SIZE,y+size1);
        g.drawLine(x+SIZE,y+size1,x+SIZE,y+size21);
        g.drawLine(x+SIZE,y+size21,x+1,y+size31);
        drawHexCentre(g,c,i);
    }

    public Dimension getMinimumSize() {
//        Point p1 = bottomRight(FillerModel.makeIndex(FillerSettings.COLUMNS-1,FillerSettings.ROWS-1));
//        Point p2 = bottomRight(FillerModel.makeIndex(FillerSettings.COLUMNS-2,FillerSettings.ROWS-1));
//        Dimension min_dim = new Dimension((p1.x < p2.x) ? p2.x : p1.x, (p1.y < p2.y) ? p2.y : p1.y);
//        min_dim.width += MIN_SIZE;
//        min_dim.height += MIN_SIZE;
//        return min_dim;
        return dim;
    }

    public Dimension getPreferredSize() {
        return dim;
    }

    public void setBoardSize(Dimension newDim) {
        dim = newDim;
        setSize(dim);
    }

    public void paintComponent(Graphics g) {
        if (off == null) resetOffscreenImage();
        g.drawImage(off,0,0,this);
    }
}
