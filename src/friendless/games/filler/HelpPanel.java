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
import javax.swing.event.*;
import java.net.*;
import java.util.*;
import friendless.awt.*;
import friendless.games.filler.player.*;
import java.util.List;

/**
 * A panel that displays a HTML page, initially "index.html".
 *
 * @author John Farrell
 */
class HelpPanel extends JEditorPane {
    PlayerWrappers players;
    String[] directories;

    public HelpPanel() {
        setLayout(new BorderLayout(4,4));
        Locale loc = Locale.getDefault();
        List dirs = new ArrayList();
        String dirName = loc.toString();
        while (true) {
            dirs.add(dirName);
            if (dirName.indexOf('_') < 0) break;
            dirName = dirName.substring(0, dirName.indexOf('_'));
        }
        if (!dirs.contains("en")) dirs.add("en");
        directories = new String[dirs.size()];
        directories = (String[]) dirs.toArray(directories);
        setEditable(false);
        addHyperlinkListener(new LinkListener());
        gotoPage("index.html");
    }

    /** Go to the named page, or else a not found page, or else the index. */
    void gotoPage(String filename) {
        try {
            URL url = findUrl(filename);
            if (url == null) url = findUrl("notfound.html");
            if (url == null) url = findUrl("index.html");
            if (url != null) {
                setPage(url);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    URL findUrl(String filename) {
        for (int i=0; i<directories.length; i++) {
            String name = "help/" + directories[i] + "/" + filename;
            URL url = getClass().getResource(name);
            if (url != null) return url;
        }
        return null;
    }

    /**
     * A listener that hears clicks on links and changes the page accordingly.
     */
    class LinkListener implements HyperlinkListener {
        public void hyperlinkUpdate(HyperlinkEvent evt) {
            if (evt.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                String u = evt.getURL().toString();
                String filename = u.substring(u.lastIndexOf('/') + 1);
                gotoPage(filename);
            }
        }
    }
}

