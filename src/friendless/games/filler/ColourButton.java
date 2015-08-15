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
import javax.swing.border.BevelBorder;

/**
 * A button with a number and a coloured background, used for the human player
 * to choose what colour he will be next.
 *
 * @author John Farrell
 */
public class ColourButton extends JButton {
    protected static final int WIDTH = 20;
    protected static final int HEIGHT = 20;
    protected int id;
    protected Color colour;

    public ColourButton(Color c, int id) {
        this.id = id;
        this.colour = c;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setOpaque(true);
        setRolloverEnabled(false);
        setText(Integer.toString(id / 2 + 1));
        setMargin(new Insets(1, 1, 1, 1));
        setFocusPainted(false);
        setBorderPainted(true);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    }

    @Override
    protected void paintComponent(Graphics g) {
        setBackground(isEnabled() ? colour : Color.lightGray);
        setForeground(FillerBoard.contrastingColour(getBackground()));
        super.paintComponent(g);
    }

    public int getID() { return id; }
}
