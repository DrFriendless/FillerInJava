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
import java.util.*;

import friendless.games.filler.remote.NetworkPanel;

/**
 * The panel which contains the whole application.
 *
 * @author John Farrell
 */
public class MainPanel extends JPanel {
    private JTabbedPane tabPane;
    private EditTournamentPanel editTourn;
    private PlayerWrappers players;
    private RankingsPanel rankings;
    private HeadToHeadPanel h2h;
    private ResourceBundle resources;
    private FillerPanel fillerPanel;
    private HelpPanel help;

    public MainPanel(ResourceBundle resources) {
        super(new BorderLayout());
        this.resources = resources;
        // load ratings
        PlayerRatings.retrieve();
        players = new PlayerWrappers(resources);
        PlayerWrappers displayPlayers = new PlayerWrappers(resources);
        tabPane = new JTabbedPane(JTabbedPane.BOTTOM);
        add(tabPane, BorderLayout.CENTER);
        fillerPanel = new FillerPanel(players, resources);
        tabPane.add(resources.getString("filler.mainpanel.name"), fillerPanel);
        tabPane.addTab(resources.getString("filler.label.tournament"), null,
                editTourn = new EditTournamentPanel(displayPlayers, resources, this),
                resources.getString("filler.string.cfgtourn"));
        tabPane.addTab(resources.getString("filler.label.rankings"),null,
            new JScrollPane(rankings = new RankingsPanel(displayPlayers)),
            resources.getString("filler.string.rankings"));
        tabPane.addTab(resources.getString("filler.string.h2h"),null,
            new JScrollPane(h2h = new HeadToHeadPanel(displayPlayers)),
            resources.getString("filler.string.h2hrec"));
        tabPane.addTab(resources.getString("filler.label.network"), null,
            new NetworkPanel(resources),
            resources.getString("filler.string.network"));
        tabPane.addTab(resources.getString("filler.label.tournamentresults"), null,
            TournamentResultsPanel.getInstance(resources),
            resources.getString("filler.string.tournamentresults"));
        tabPane.addTab(resources.getString("filler.label.help"),null,
                new JScrollPane(help = new HelpPanel(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                resources.getString("filler.string.help"));
        tabPane.addChangeListener(e -> refreshTabs());
    }

    /**
     * Make sure that the current tab is displaying up to date information.
     */
    void refreshTabs() {
        int index = tabPane.getSelectedIndex();
        String name = tabPane.getTitleAt(index);
        if (name.equals(resources.getString("filler.label.rankings"))) {
            rankings.refresh();
        } else if (name.equals(resources.getString("filler.string.h2h"))) {
            h2h.refresh();
        }
    }

    public Dimension getPreferredSize() { return new Dimension(680,525); }

    public void playTournament(TournamentRules rules, PlayerWrappers tournPlayers) {
        tabPane.setSelectedComponent(fillerPanel);
        fillerPanel.playTournament(rules, tournPlayers);
    }
}
