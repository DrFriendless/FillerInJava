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
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import friendless.awt.*;
import friendless.awt.SplashScreen;

/**
 * Main class for the game.
 *
 * @author John Farrell
 */
public final class Filler implements Runnable {
    JPanel panel;
    static ResourceBundle resources;

    public void run() {
        // create a frame to run in
        JFrame frame = new JFrame(resources.getString("filler.title"));
        panel = new MainPanel(resources);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { close(); }
        });
        frame.getContentPane().add("Center", panel);
        frame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        frame.setLocation((screenSize.width - frameSize.width)/2,
            (screenSize.height - frameSize.height)/2);
        frame.setVisible(true);
    }

    /** User clicked on the kill window button. */
    void close() {
        System.exit(0);
    }

    public static void main(String[] argv) {
        // This code copied from the Locale static initialiser, because it
        // doesn't seem to be executed in Sun's JDK1.3 on Linux.
        String language = System.getProperty("user.language","en");
        String country = System.getProperty("user.region","");
        // language can be en_US if set from $LANG on Linux
        int i = language.indexOf('_');
        if (i >= 0) {
            if (country.equals("")) {
                country = language.substring(i+1);
            }
            language = language.substring(0, i);
        }
        String variant = "";
        i = country.indexOf('_');
        if (i >= 0) {
            variant = country.substring(i+1);
            country = country.substring(0, i);
        }
        Locale defaultLocale = new Locale(language, country, variant);
        Locale.setDefault(defaultLocale);
        // load resources
        resources = ResourceBundle.getBundle("friendless.games.filler.resources", defaultLocale);
        ImageIcon splashIcon = new ImageIcon(Filler.class.getResource(resources.getString("filler.filename.splash")));
        SplashScreen splash = SplashScreen.show(splashIcon);
        EloRating.setResources(resources);
        Tournaments.setResources(resources);
        Filler f = new Filler();
        f.run();
        splash.close();
        splash = null;
    }
}
