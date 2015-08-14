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
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import friendless.awt.*;
import friendless.games.filler.player.*;

/**
 * The panel which lets you choose tournament rules.
 *
 * @author John Farrell
 */
class EditTournamentPanel extends JPanel {
    /** Radio buttons to let you choose the tournament type. */
    private JRadioButton robin, knock, basho, challenge;
    /** Whether the tournament is continuous or not. */
    private JCheckBox continuous;
    /** ButtonGroup for the radio buttons. */
    private ButtonGroup ruleGroup;
    private ChoosePlayerList playerList;
    /** All the players that can be chosen. */
    private PlayerWrappers players;
    /** Reference to resources file. */
    private ResourceBundle resources;
    /** Command to play tournament. */
    private JButton playTournament;
    /** Description of last selected player. */
    private JTextArea description;
    /** Description of current tournament. */
    private JTextArea tournDesc;
    /** The MainPanel*/
    private MainPanel mainPanel;
    private SetTournamentDescriptionListener std = new SetTournamentDescriptionListener();

    public EditTournamentPanel(PlayerWrappers players, final ResourceBundle resources,
            MainPanel mainPanel) {
        this.resources = resources;
        this.players = players;
        players.sortByRatings();
        this.mainPanel = mainPanel;
        setBorder(new EmptyBorder(4,4,4,4));
        setLayout(new GridLayout(1,3));
        JPanel p;
        JScrollPane scroll;
        add(p = new JPanel(new VCodeLayout("f",4)));
        JPanel rulesPanel = new JPanel(new VCodeLayout("l", 4));
        rulesPanel.setBorder(BorderFactory.createTitledBorder(resources.getString("filler.label.tournrules")));
        ruleGroup = new ButtonGroup();
        rulesPanel.add("",robin = new JRadioButton(resources.getString("filler.label.roundrobin")));
        ruleGroup.add(robin);
        robin.setSelected(true);
        robin.addActionListener(std);
        rulesPanel.add("",knock = new JRadioButton(resources.getString("filler.label.knockout")));
        ruleGroup.add(knock);
        knock.addActionListener(std);
        rulesPanel.add("",basho = new JRadioButton(resources.getString("filler.label.basho")));
        ruleGroup.add(basho);
        basho.addActionListener(std);
        rulesPanel.add("",challenge = new JRadioButton(resources.getString("filler.label.challenge")));
        ruleGroup.add(challenge);
        challenge.addActionListener(std);
        p.add("", rulesPanel);
        p.add(continuous = new JCheckBox(resources.getString("filler.label.continuous")));
        p.add("x", tournDesc = new JTextArea(""));
        tournDesc.setBorder(BorderFactory.createTitledBorder(resources.getString("filler.label.description")));
        tournDesc.setEditable(false);
        tournDesc.setLineWrap(true);
        tournDesc.setWrapStyleWord(true);
        tournDesc.setToolTipText(resources.getString("filler.string.tourndescription"));
        tournDesc.setBackground(getBackground());
        //
        playerList = new ChoosePlayerList(players, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(scroll = new JScrollPane(playerList));
        playerList.setToolTipText(resources.getString("filler.string.choose2edit"));
        playerList.setBackground(getBackground());
        playerList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                if (evt.getValueIsAdjusting()) return;
                JList list = (JList) evt.getSource();
                int index = list.getAnchorSelectionIndex();
                PlayerWrapper player = (PlayerWrapper) list.getModel().getElementAt(index);
                Object[] args = { player.getName() };
                String text = MessageFormat.format(player.getDescription(), args);
                description.setText(text);
            }
        });
        scroll.setBorder(BorderFactory.createTitledBorder(resources.getString("filler.label.players")));
        add(p = new JPanel(new VCodeLayout("",4)));
        p.add("x", description = new JTextArea());
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setToolTipText(resources.getString("filler.string.playerdescription"));
        description.setBorder(BorderFactory.createTitledBorder(resources.getString("filler.label.description")));
        description.setBackground(getBackground());
        p.add("x", new JPanel());
        p.add("", playTournament = new JButton(resources.getString("filler.label.playtournament")));
        playTournament.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                EditTournamentPanel.this.mainPanel.playTournament(getRules(), getSelectedPlayers());
            }
        });
        setTournamentDescription("roundrobin");
    }

    PlayerWrappers getSelectedPlayers() {
        return players.getSelected();
    }

    /** Look at the control settings to determine the rules of the tournament. */
    TournamentRules getRules() {
        TournamentRules rules = null;
        if (robin.isSelected()) {
            rules = new TournamentRules(TournamentRules.ROUND_ROBIN);
        } else if (knock.isSelected()) {
            rules = new TournamentRules(TournamentRules.KNOCKOUT);
        } else if (basho.isSelected()) {
            rules = new TournamentRules(TournamentRules.BASHO);
            rules.bashoRounds = players.size() * 3 / 8;
        } else if (challenge.isSelected()) {
            rules = new TournamentRules(TournamentRules.CHALLENGE);
        }
        rules.setContinuous(continuous.isSelected());
        return rules;
    }

    /** Set the description field for the given type of tournament. */
    void setTournamentDescription(String key) {
        try {
            String desc = resources.getString("description." + key);
            tournDesc.setText(desc);
        } catch (MissingResourceException ex) {
            tournDesc.setText("");
        }
    }

    /**
     * A class which listents to the tournament buttons and sets the correct
     * description.
     */
    class SetTournamentDescriptionListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            Object source = evt.getSource();
            String key = null;
            if (source == basho) {
                key = "basho";
            } else if (source == knock) {
                key = "knockout";
            } else if (source == challenge) {
                key = "challenge";
            } else if (source == robin) {
                key = "roundrobin";
            }
            setTournamentDescription(key);
        }
    }
}
