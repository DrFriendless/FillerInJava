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

package friendless.awt;

import java.awt.*;
import javax.swing.*;

/**
 * A splash screen.
 * This is just a Window with an ImageIcon in it.
 *
 * @author John Farrell
 */
public class SplashScreen extends Window {
    Frame frame;

    private SplashScreen(Frame frame, ImageIcon img) {
        super(frame);
        this.frame = frame;
        add("Center",new JLabel(img));
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width-img.getIconWidth())/2,(screenSize.height-img.getIconHeight())/2);
    }

    public void close() {
        setVisible(false);
        dispose();
        frame.dispose();
    }

    public static SplashScreen show(ImageIcon img) {
        SplashScreen ss = new SplashScreen(new Frame("dummy"),img);
        ss.setVisible(true);
        return ss;
    }
}
