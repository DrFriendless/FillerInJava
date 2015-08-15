package friendless.games.filler.player;

import friendless.games.filler.RobotPlayer;

/**
 * Bronwyn chooses randomly from the colours which are actually on her border.
 *
 * Created by john on 15/08/15.
 */
public class Bronwyn extends RobotPlayer {
    public String getName() { return "Bronwyn"; }

    public int turn() { return randomBorderTurn(); }

    public String getIcon() { return "greenAlien.gif"; }
}
