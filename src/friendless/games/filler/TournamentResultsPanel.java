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
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import friendless.games.filler.player.*;

/**
 * A Panel to show results of a running tournament
 * <P>
 * TournamentResultsPanel uses the Singleton Design Pattern. This means that instead of constructing using new,
 * you call the getInstance method to access the single instance. e.g. TournamentResultsPanel.getInstance(resources).clearText()
 * @author Lang Sharpe
 */
public class TournamentResultsPanel extends JPanel {
    private JTextArea textArea;
    private JScrollPane textScroll;
    private JTable pointsTable;
    private TournamentPointsTable tableModel;
    private JScrollPane tableScroll;
    private static TournamentResultsPanel instance;
    private ResourceBundle resources;

    private TournamentResultsPanel(ResourceBundle resources) {
        super();
        this.resources = resources;
        textArea = new JTextArea("Results from any Tournaments will be displayed here");
        textArea.setEditable(false);
        textScroll = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add("Center", textScroll);
    }

    /**
     * @return the Singleton instance
     */
    public static TournamentResultsPanel getInstance(ResourceBundle resources) {
        if (instance == null) {
            instance = new TournamentResultsPanel(resources);
        }
        return instance;
    }

    /** Add text to text area of panel */
    public void addText(String str) {
        String temp = textArea.getText();
        textArea.setText(temp+str);
    }

    /** Remove text from text area of panel */
    public void clearText() {
        textArea.setText("");
    }

    /** Add match to round robin table. Call must be preceded by newTournament with round robin as rules */
    public void addMatch(PlayerWrapper[] players, int[] scores){
        tableModel.addMatch(players, scores);
    }

    /** Call when a tournament is about to begin. The panel will be adjusted to get ready to display results */
    public void newTournament(TournamentRules rules,PlayerWrappers players){
        switch (rules.rules) {
            case TournamentRules.ROUND_ROBIN:
                this.removeAll();
                tableModel = new TournamentPointsTable(resources, players);
                pointsTable = new JTable(tableModel);
                tableScroll.setViewportView(pointsTable);
                tableScroll.setPreferredSize(new Dimension(1, pointsTable.getRowHeight() * (pointsTable.getRowCount() +1)));
                setLayout(new BorderLayout(4,4));
                add("Center", new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, textScroll ));
                break;
            case TournamentRules.BASHO:
            case TournamentRules.KNOCKOUT:
            case TournamentRules.CHALLENGE:
            default:
                this.removeAll();
                setLayout(new BorderLayout(4,4));
                add("Center", textScroll);
                break;
        }
        this.clearText();
    }
}

