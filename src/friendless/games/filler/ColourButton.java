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
 * A button with a number and a coloured background, used for the human player
 * to choose what colour he will be next.
 *
 * @author John Farrell
 */
public class ColourButton extends JRadioButton {
    protected static final int WIDTH = 20;
    protected static final int HEIGHT = 20;
    protected static final Font numFont = new Font("dialog", Font.BOLD, 10);
    protected int id, xoff, yoff;
    protected String label;
    protected Image normal, disabled;
    protected Color colour;

    public ColourButton(Color c, int id) {
        super();
        setBorderPainted(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setRolloverEnabled(false);
        this.id = id;
        this.label = Integer.toString(id/2+1);
        this.colour = c;
        setMargin(new Insets(0,0,0,0));
    }

    public void addNotify() {
        super.addNotify();
        FontMetrics fm = getFontMetrics(numFont);
        xoff = (WIDTH - fm.stringWidth(label) + 1)/2;
        yoff = 14;
        // create images
        Graphics goff;
        // normal image
        normal = createImage(WIDTH,HEIGHT);
        goff = normal.getGraphics();
        normalPaintIcon(this,goff);
        goff.dispose();
        setIcon(new ImageIcon(normal));
        // disabled image
        disabled = createImage(WIDTH,HEIGHT);
        goff = disabled.getGraphics();
        disabledPaintIcon(this,goff);
        goff.dispose();
        setDisabledIcon(new ImageIcon(disabled));
    }

    /** Draw the normal icon for this button. */
    public void normalPaintIcon(Component c, Graphics g) {
        g.setColor(colour);
        drawInside(c,g);
        g.setColor(FillerBoard.contrastingColour(colour));
        g.setFont(numFont);
        g.drawString(label, xoff, yoff);
    }

    /** Draw the disabled icon for this button. */
    public void disabledPaintIcon(Component c, Graphics g) {
        g.setColor(Color.lightGray);
        drawInside(c,g);
    }

    /** Draw the coloured part for this icon. */
    protected void drawInside(Component c, Graphics g) {
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    public int getID() { return id; }
}
