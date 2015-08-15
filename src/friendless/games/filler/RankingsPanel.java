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
import javax.swing.text.*;
import javax.swing.text.Style;

/**
 * The panel that lists the players' rankings.
 *
 * @author John Farrell
 */
class RankingsPanel extends JPanel {
    /** The component that the rankings document is displayed in. */
    JTextPane text;
    /** The players to be listed. */
    PlayerWrappers players;
    /** Styles which can be used in the document. */
    StyleContext styles;

    public RankingsPanel(PlayerWrappers players) {
        super(true);
        this.players = players;
        setLayout(new BorderLayout(4,4));
        styles = new StyleContext();
        createStyles();
        add("Center",text = new JTextPane());
        text.setEditable(false);
    }

    /**
     * The panel is about to be redisplayed so update the information in the
     * document.
     */
    public void refresh() {
        players.sortByRatings();
        DefaultStyledDocument doc = new DefaultStyledDocument(styles);
        int high = Integer.MAX_VALUE;
        int low = 0;
        try {
            for (int i=EloRating.TITLES.length-1; i>=0; i--) {
                low = EloRating.RATINGS[i];
                String range = Integer.toString(low);
                if (high == Integer.MAX_VALUE) {
                    range += "+";
                } else {
                    range += "-" + Integer.toString(high-1);
                }
                doc.insertString(doc.getLength(), EloRating.TITLES[i] + " (" + range + ")", styles.getStyle("heading"));
                doc.insertString(doc.getLength(), "\n", null);
                for (int j=0; j<players.size(); j++) {
                    int rating = players.get(j).getRating();
                    if ((rating < high) && (rating >= low)) {
                        doc.insertString(doc.getLength(),Integer.toString(rating),styles.getStyle("number"));
                        doc.insertString(doc.getLength()," " + players.get(j).getName(),styles.getStyle("normal"));
                        doc.insertString(doc.getLength(), "\n", null);
                    }
                }
                doc.insertString(doc.getLength(), "\n", null);
                high = low;
            }
            text.setDocument(doc);
            //revalidate();
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    void createStyles() {
        // default style
        Style def = styles.getStyle(StyleContext.DEFAULT_STYLE);
        // heading style
        Style heading = styles.addStyle("heading", def);
        //StyleConstants.setForeground(heading, FillerContainer.FILLER_COLOUR);
        StyleConstants.setFontFamily(heading, "SansSerif");
        StyleConstants.setBold(heading, true);
        StyleConstants.setAlignment(heading, StyleConstants.ALIGN_CENTER);
        StyleConstants.setSpaceAbove(heading, 10);
        StyleConstants.setSpaceBelow(heading, 16);
        StyleConstants.setFontSize(heading, 18);
        // normal
        Style sty = styles.addStyle("normal", def);
        //StyleConstants.setForeground(sty, FillerContainer.FILLER_COLOUR_DARKER);
        StyleConstants.setBold(sty, true);
        StyleConstants.setLeftIndent(sty, 10);
        StyleConstants.setRightIndent(sty, 10);
        StyleConstants.setFontFamily(sty, "SansSerif");
        StyleConstants.setFontSize(sty, 14);
        StyleConstants.setSpaceAbove(sty, 4);
        StyleConstants.setSpaceBelow(sty, 4);
        // numbers
        Style number = styles.addStyle("number",sty);
        StyleConstants.setFontFamily(number, "monospaced");
    }
}
