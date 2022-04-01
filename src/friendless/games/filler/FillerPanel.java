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
import java.io.*;
import java.text.*;
import java.util.*;
import friendless.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentAdapter;
// for remote player
import friendless.games.filler.remote.RemoteConnection;
import friendless.games.filler.remote.messages.MoveMessage;
import friendless.games.filler.player.RemotePlayer;

/**
 * The panel which holds the FillerBoard and all the associated buttons and
 * combo boxes and so on.
 *
 * @author John Farrell
 */
public final class FillerPanel extends JPanel implements KeyListener {
    private static final String SEP = System.getProperty("line.separator");
    /** The actual coloured hexes. */
    protected FillerBoard board;
    /**
     * The current player, while a game is in progress.
     * This is used to determine what player is executing the actions when the
     * GUI input methods (keys, colour buttons) are enabled.
     */
    protected FillerPlayer currentPlayer;
    protected JPanel[] buttonPanels;
    protected JLabel[] scoreLabels;
    /** The combo boxes that let you choose opponents. */
    protected PlayerComboBox[] playerNames;
    protected JButton startButton;
    protected JButton cancelButton;
    /** Button groups for the colour buttons. */
    protected ButtonGroup[] buttonGroups;
    /** The colour buttons. */
    protected ColourButton[] buttons;
    /** All the players that there are. */
    protected PlayerWrappers players;
    /** Whether the cancelled button has been pressed. */
    protected volatile boolean cancelled;
    protected JLabel message1, message2;
    protected CardLayout cards;
    protected JPanel cardPanel;
    protected ResourceBundle resources;

    public FillerPanel(PlayerWrappers players, ResourceBundle resources) {
        this.players = players;
        this.resources = resources;
        setLayout(new VCodeLayout("f",0));
        // create all the buttons
        buttonPanels = new JPanel[2];
        for (int i=0; i<2; i++) {
            buttonPanels[i] = new JPanel(new HCodeLayout("c",2));
            buttonPanels[i].addKeyListener(this);
        }
        buttons = new ColourButton[FillerSettings.NUM_COLOURS * 2];
        buttonGroups = new ButtonGroup[] { new ButtonGroup(), new ButtonGroup() };
        int k = -1;
        for (int i=0; i<FillerSettings.NUM_COLOURS; i++) {
            for (int j=0; j<2; j++) {
                buttons[++k] = new ColourButton(FillerSettings.colours[i],k);
                buttonGroups[j].add(buttons[k]);
                buttonPanels[j].add(buttons[k]);
                buttons[k].addActionListener(evt -> colourButtonClicked((ColourButton) evt.getSource()));
            }
        }
        // create the labels
        scoreLabels = new JLabel[2];
        Font f = new Font("SansSerif",Font.BOLD,13);
        for (int i=0; i<2; i++) {
            scoreLabels[i] = new JLabel("000",JLabel.RIGHT);
            scoreLabels[i].setFont(f);
            scoreLabels[i].setForeground(Color.black);
            Dimension d = scoreLabels[i].getPreferredSize();
            scoreLabels[i].setPreferredSize(d);
            scoreLabels[i].setBorder(BorderFactory.createEmptyBorder());
        }
        // playerPanel has combo boxes and cancel button
        JPanel playerPanel = new JPanel(new HCodeLayout("f",4));
        playerPanel.setBorder(new EmptyBorder(2,2,2,2));
        add("", playerPanel);
        // scorePanel has scores, messages and buttons
        JPanel scorePanel = new JPanel(new HCodeLayout("", 4));
        scorePanel.add("", scoreLabels[0]);
        scorePanel.add("x", cardPanel = new JPanel(cards = new CardLayout()));
        scorePanel.add("", scoreLabels[1]);
        // cardPanel buttons or messages
        cardPanel.setBorder(new EmptyBorder(2,2,2,2));
        // buttons panel
        JPanel topPanel = new JPanel(new HCodeLayout("c",4));
        cardPanel.add("buttons", topPanel);
        // messages panel
        JPanel p1 = new JPanel(new HCodeLayout("f",4));
        p1.add("", message1 = new JLabel());
        p1.add("x", new JLabel());
        p1.add("", message2 = new JLabel());
        message2.setHorizontalTextPosition(JLabel.RIGHT);
        message1.setForeground(Color.black);
        message2.setForeground(Color.black);
        cardPanel.add("messages", p1);
        add("", scorePanel);
        // create the player name choices
        playerNames = new PlayerComboBox[] {
            new PlayerComboBox(players, resources),
            new PlayerComboBox(players, resources)
        };
        // player panel
        JPanel p;
        playerPanel.add("", playerNames[0]);
        playerPanel.add("x", p = new JPanel());
        playerPanel.add("", cancelButton = new JButton(resources.getString("filler.label.cancel")));
        cancelButton.addActionListener(event -> cancel());
        cancelButton.setEnabled(false);
        playerPanel.add("", playerNames[1]);
        playerPanel.add("", startButton = new JButton(resources.getString("filler.label.play")));
        startButton.addActionListener(event -> playGame());
        // buttons panel
        topPanel.add("", buttonPanels[0]);
        topPanel.add("x", p = new JPanel());
        topPanel.add("", buttonPanels[1]);
        // complete panel
//        JPanel p2 = new JPanel(new HCodeLayout("", 0));
//        p2.add("x", new JPanel());
//        p2.add("x", board = new FillerBoard()); //, BorderLayout.CENTER
//        p2.add("x", new JPanel());
//        add("", p2);
        board = new FillerBoard();
        add("", board);
        showButtons();

        FillerPanel me = this;
        me.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //System.out.printf("was: x=%d y=%d w=%d h=%d\n",
                //    board.getLocation().x, board.getLocation().y, board.getSize().width, board.getSize().height);
                Dimension xSize = me.getSize();
                Point p2Location = board.getLocation();
                xSize.height-= p2Location.y;
                board.setBoardSize(xSize);
                //System.out.printf("set: width=%d height=%d\n", xSize.width, xSize.height);
            }
        });


    }

    public void showMessage(String s1, String s2) {
        message1.setText(s1);
        message2.setText(s2);
        cards.show(cardPanel, "messages");
    }

    public void showButtons() {
        cards.show(cardPanel, "buttons");
    }

    public FillerBoard getBoard() { return board; }

    /** Cancel a tournament in progress. */
    protected void cancel() {
        cancelled = true;
        cancelButton.setEnabled(false);
        Tournaments.cancel();
    }

    protected void playTournament(final TournamentRules rules, final PlayerWrappers tournPlayers) {
        startButton.setEnabled(false);
        cancelButton.setEnabled(true);
        playerNames[0].setEnabled(false);
        playerNames[1].setEnabled(false);
        cancelled = false;
        Thread t = new Thread(() -> {
                Tournaments.tournament(rules, FillerPanel.this, tournPlayers);
                finish();
            });
        t.start();
    }

    protected void playGame() {
        startButton.setEnabled(false);
        cancelButton.setEnabled(true);
        playerNames[0].setEnabled(false);
        playerNames[1].setEnabled(false);
        cancelled = false;
        Thread t = new Thread(() -> game());
        t.start();
    }

    /** Must be called from game thread **/
    protected void finish() {
        startButton.setEnabled(true);
        cancelButton.setEnabled(false);
        playerNames[0].setEnabled(true);
        playerNames[1].setEnabled(true);
        PlayerRatings.save();
    }

    /**
     * Notification that one of the coloured buttons was clicked. As they are
     * only enabled when the current player is interactive and is having a turn,
     * this means that their turn is to choose the given colour.
     */
    public void colourButtonClicked(ColourButton cb) {
        if (currentPlayer == null) return;
        FillerPlayer fp = currentPlayer;
        currentPlayer = null;
        boolean ok = fp.colourChosen(cb.getID() / 2);
        if (!ok) currentPlayer = fp;
    }

    public void game() {
        showButtons();
        PlayerWrapper[] opponents = new PlayerWrapper[2];
        for (int i=0; i<2; i++) {
            opponents[i] = (PlayerWrapper) playerNames[i].getSelectedItem();
        }
        int[] scores = play(opponents);
        String winner = (scores[0] > scores[1]) ? opponents[0].getName() : opponents[1].getName();
        String h2h = resources.getString("filler.string.h2h") + ": " +
            PlayerRatings.getHeadToHead(opponents);
        String mesg = MessageFormat.format(resources.getString("filler.string.winner"), winner);
        showMessage(mesg, h2h);
        finish();
    }

    /**
     * Play a tournament match between the given pair of players.
     * @return the scores of the players in the same order as they are in
     * <code>players</code>.
     * This method sets up the combo boxes before the game, and displays
     * the victory details afterwards.
     */
    public int[] tournamentMatch(PlayerWrapper[] players) {
        if (players[0] == null) {
            return new int[] { -1, 0 };
        } else if (players[1] == null) {
            return new int[] { 0, -1 };
        }
        playerNames[0].setSelectedItem(players[0]);
        playerNames[1].setSelectedItem(players[1]);
        playerNames[0].repaint();
        playerNames[1].repaint();
        int[] scores = play(players);
        int winner = (scores[0] > scores[1]) ? 0 : 1;
        int loser = 1 - winner;
        String h2h = resources.getString("filler.string.h2h") + ": " + PlayerRatings.getHeadToHead(players);
        String mesg = resources.getString("filler.string.winner");
        mesg = MessageFormat.format(mesg,  players[winner].getName());
        showMessage(mesg, h2h);
        String template = resources.getString("filler.string.matchresult");
        Object[] args = { players[winner].getName(), players[loser].getName(), scores[winner], scores[loser] };
        mesg = MessageFormat.format(template, args);
        TournamentResultsPanel.getInstance(resources).addText(mesg + SEP);
        PlayerRatings.save();
        return scores;
    }

    public int[] play(PlayerWrapper[] players) {
        int turn = 0;
        int[] score = new int[2];
        int[] colours = new int[] {-1, -1};
        Thread.currentThread().setPriority(3);
        FillerSpace space[] = new FillerSpace[] { new FillerSpace(), new FillerSpace() };
        FillerPlayer[] opponents = new FillerPlayer[] { players[0].getInstance(), players[1].getInstance() };
        // isRemote == -1: if it's not a remote game
        // isRemote == 0: if player 0 is the remote player
        // isRemote == 1: if player 1 is the remote player
        int isRemote = -1;
        if (opponents[0] instanceof RemotePlayer) {
            isRemote = 0;
        } else if (opponents[1] instanceof RemotePlayer) {
            isRemote = 1;
        }
        board.restart(isRemote != -1);
        colours[0] = board.model.pieces[FillerSettings.ORIGINS[0]];
        colours[1] = board.model.pieces[FillerSettings.ORIGINS[1]];
        board.repaint();
        // initialise players
        for (int i=0; i<2; i++) {
            opponents[i].setOrigin(FillerSettings.ORIGINS[i],FillerSettings.ORIGINS[1-i]);
            score[i] = board.countScore(FillerSettings.ORIGINS[i], space[i]);
            scoreLabels[i].setText(Integer.toString(score[i]));
        }
        int[] rs = PlayerRatings.getRatings(players[0], players[1]);
        boolean[] requiresButtons =
            { opponents[0].requiresButtons(), opponents[1].requiresButtons() };
        boolean fast = !requiresButtons[0] && !requiresButtons[1];
        int i = 0;
        showButtons();
        while (true) {
            currentPlayer = opponents[i];
            buttonPanels[i].requestFocus();
            for (int k=0; k<FillerSettings.NUM_COLOURS*2; k++) {
                if (!requiresButtons[k%2]) {
                    buttons[k].setEnabled(false);
                } else if (k % 2 == i) {
                    ButtonModel bm = buttons[k].getModel();
                    bm.setEnabled(k/2 != colours[1-i]);
                    bm.setPressed(k/2 != colours[i]);
                } else {
                    buttons[k].setEnabled(false);
                }
            }
            int oldColour = colours[i];
            colours[i] = opponents[i].takeTurn(board.model,colours[1-i]);
            if (isRemote != -1 && i != isRemote) {
                // Opponent is a remote player, send local move to remote
                try {
                    MoveMessage msg = new MoveMessage(colours[i]);
                    RemoteConnection.getInstance().sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (colours[i] == colours[1-i] || colours[i] < 0 || colours[i] >= FillerSettings.NUM_COLOURS) {
                // player chose an invalid myColour
                System.out.println(opponents[i].getName() + " chose " + colours[i]);
                colours[i] = oldColour;
            }
            score[i] = board.changeColourCountScore(space[i],colours[i],FillerSettings.ORIGINS[i], fast);
            // currentPlayer may now be null
            scoreLabels[i].setText(Integer.toString(score[i]));
            if (score[i] >= FillerSettings.POINTS_TO_WIN) break;
            turn += (i % 2);
            i = 1-i;
        }
        if (score[0] >= FillerSettings.POINTS_TO_WIN || score[1] >= FillerSettings.POINTS_TO_WIN) {
            PlayerRatings.setRatings(players, rs, (score[0] > score[1]) ? 0 : 1);
        }
        return score;
    }

    /**
     * This method notifies that a key was typed. This implements the use
     * of the keyboard to choose one of the colour buttons.
     */
    public void keyTyped(KeyEvent e) {
        // only '0' to '8' are valid
        char c = e.getKeyChar();
        if (!Character.isDigit(c) || (c == '0')) return;
        // must be someone's turn
        if (currentPlayer == null) return;
        FillerPlayer fp = currentPlayer;
        currentPlayer = null;
        boolean ok = fp.colourChosen(c - '1');
        if (!ok) currentPlayer = fp;
    }

    /** Null implementation for the KeyListener interface. */
    public void keyPressed(KeyEvent e) { }

    /** Null implementation for the KeyListener interface. */
    public void keyReleased(KeyEvent e) { }
}
