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

import java.awt.event.*;
import javax.swing.*;

/**
 * A FillerBoard in a popup window.
 * This is very useful for debugging.
 * It is not used in the normal game.
 *
 * @author John Farrell
 */
public class PopupFillerBoard extends FillerBoard {
    protected JFrame frame;

    public PopupFillerBoard(FillerModel model) {
        super(model);
    }

    public void popdown() {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
    }

    public void popup(String name) {
        frame = new JFrame(name);
        frame.getContentPane().add("Center", this);
        frame.pack();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { popdown(); }
        });
        frame.setVisible(true);
    }

    public static void popup(FillerModel model, String name) {
        (new PopupFillerBoard(model)).popup(name);
    }
}
