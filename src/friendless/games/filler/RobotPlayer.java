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

import java.util.*;

/**
 * A computer player.
 * Most of the basic strategic moves for medium skill robots are implemented here.
 *
 * @author John Farrell
 */
public abstract class RobotPlayer extends DumbRobotPlayer {
    protected final BitSet NO_COLOURS = new BitSet(FillerSettings.NUM_COLOURS);
    protected FillerPlayerSpace space;
    protected FillerModel model;
    protected int score;
    protected int turn;
    protected int realScore;

    protected RobotPlayer() {
        space = new FillerPlayerSpace();
        score = 0;
    }

    /**
     * This method is part of the implementation of the FillerPlayer interface.
     * It does some calculations to help the strategies defined in this class,
     * and delegates the actual choice of move to the abstract method
     * <code>turn()</code>.
     */
    public int takeTurn(FillerModel model, int otherPlayerColour) {
        turn++;
        this.model = model;
        this.otherPlayerColour = otherPlayerColour;
        calculate(model);
        setScores();
        colour = turn();
        if (colour < 0) {
            colour = random_turn();
            // a debugging message - this suggests that your algorithm doesn't cover enough bizarre cases,
            // or that it's the end of the game and the player cannot make a move to get points.
            System.out.println(getName() + " chooses randomly in takeTurn");
        }
        return colour;
    }

    /**
     * Figure out who has what influence over each hex.
     */
    protected void calculate(FillerModel model) {
        FillerModel.allocateTypes(model, origins, space);
        //PopupFillerBoard.popup(new FillerModel(space.counted), "calculated");
    }

    /** Figure out the score given the current counting in space */
    protected void setScores() {
        int[] counted = space.counted;
        score = 0;
        realScore = 0;
        for (int c : counted) {
            switch (c) {
                case FillerModel.MINE:
                    score++;
                    realScore++;
                    break;
                case FillerModel.REACHABLE:
                    realScore++;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Count the number of pieces of each colour for all of the types defined.
     * Make sure that the other player's colour gets a bad score.
     */
    int[] countSet(BitSet allowed) {
        int[] counted = space.counted;
        int[] count = new int[FillerSettings.NUM_COLOURS];
        for (int i=0; i<counted.length; i++) {
            if (allowed.get(counted[i])) count[model.pieces[i]]++;
        }
        // make sure the other player's colour is not chosen
        count[otherPlayerColour] = -1;
        return count;
    }

    /**
     * @return the colour which has the highest occurrence in the types defined
     * in the set <code>allowed</code>.
     */
    int mostInSetTurn(BitSet allowed) {
        int[] count = countSet(allowed);
        // choose any of the best colours
        BitSet favourites = null;
        int best = 0;
        for (int i=0; i<FillerSettings.NUM_COLOURS; i++) {
            if (count[i] <= 0) continue;
            if (count[i] > best) {
                favourites = (BitSet) NO_COLOURS.clone();
                favourites.set(i);
                best = count[i];
            } else if (count[i] == best) {
                favourites.set(i);
            }
        }
        return chooseRandom(favourites);
    }

    /** Chooses a colour to get the most points immediately **/
    public int mostTurn() {
        BitSet b = (BitSet) NO_COLOURS.clone();
        b.set(FillerModel.BORDER);
        b.set(FillerModel.SHARED_BORDER);
        b.set(FillerModel.INTERNAL_BORDER);
        return mostInSetTurn(b);
    }

    /**
     * Chooses a colour to get the most points in this turn from free space,
     * i.e. that which is available to this player and to the opponent.
     */
    public int mostFreeTurn() {
        BitSet b = (BitSet) NO_COLOURS.clone();
        b.set(FillerModel.BORDER);
        b.set(FillerModel.SHARED_BORDER);
        return mostInSetTurn(b);
    }

    /**
     * Chooses the colour that would get the opponent the most free space in
     * his next turn.
     */
    public int opponentMostTurn() {
        BitSet b = (BitSet) NO_COLOURS.clone();
        b.set(FillerModel.HIS_BORDER);
        b.set(FillerModel.SHARED_BORDER);
        return mostInSetTurn(b);
    }


    /**
     * Aims to achieve a nominated target.
     * If the target is occupied, get as close as possible.
     */
    public int targetTurn(int target) {
        if (target < 0) return -1;
        int[] counted = space.counted;
        int closest = 1000;
        int favourite = -1;
        for (int i=0; i<counted.length; i++) {
            switch (counted[i]) {
                case FillerModel.BORDER:
                case FillerModel.SHARED_BORDER:
                    int dist = diagDistance(target,i);
                    if ((dist < closest) && (model.pieces[i] != otherPlayerColour)) {
                        favourite = model.pieces[i];
                        closest = dist;
                    }
                    break;
                default: break;
            }
        }
        return favourite;
    }

    public int expandTurn() {
        int[] counted = space.counted;
        int furthest = -1;
        int favourite = -1;
        int o = origins[0];
        for (int i=0; i<counted.length; i++) {
            switch (counted[i]) {
                case FillerModel.BORDER:
                case FillerModel.SHARED_BORDER:
                    int dist = sideDistance(i,o);
                    if ((dist > furthest) && (model.pieces[i] != otherPlayerColour)) {
                        favourite = model.pieces[i];
                        furthest = dist;
                    }
                    break;
            }
        }
        return favourite;
    }

    public int furthestBorderTurn() {
        int[] counted = space.counted;
        int furthest = -1;
        int favourite = -1;
        int o = origins[0];
        for (int i=0; i<counted.length; i++) {
            switch (counted[i]) {
                case FillerModel.BORDER:
                case FillerModel.SHARED_BORDER:
                    if (FillerModel.isPerimeter(i) && model.pieces[i] != otherPlayerColour) {
                        int dist = sideDistance(i,o);
                        if (dist > furthest) {
                            favourite = model.pieces[i];
                            furthest = dist;
                        }
                    }
            }
        }
        return favourite;
    }

    public int borderTurn() {
        int[] counted = space.counted;
        for (int i=0; i<counted.length; i++) {
            switch (counted[i]) {
                case FillerModel.BORDER:
                case FillerModel.SHARED_BORDER:
                    if (FillerModel.isPerimeter(i) && model.pieces[i] != otherPlayerColour) {
                        return model.pieces[i];
                    }
            }
        }
        return -1;
    }

    protected int dontExpandTurn() {
        int[] counted = space.counted;
        int closest = Integer.MAX_VALUE;
        int favourite = -1;
        for (int i=0; i<counted.length; i++) {
            if (counted[i] == FillerModel.BORDER || counted[i] == FillerModel.SHARED_BORDER || counted[i] == FillerModel.REACHABLE) {
                int dist = diagDistance(i, origins[0]);
                if (dist < closest && model.pieces[i] != otherPlayerColour) {
                    favourite = model.pieces[i];
                    closest = dist;
                }
            }
        }
        return favourite;
    }

    /**
     * Return the best colour to enable this player to reach the goal point. If the
     * goal point is already owned, or unreachable, return -1.
     */
    public int goalTurn(int goal) {
        if (goal < 0) return -1;
        int[] counted = space.counted;
        if ((counted[goal] == FillerModel.MINE) ||
            (counted[goal] == FillerModel.HIS_REACHABLE) ||
            (counted[goal] == FillerModel.HIS)) {
            return -1;
        }
        int favourite = -1;
        int closest = 1000;
        for (int i=0; i<counted.length; i++) {
            if ((counted[i] == FillerModel.BORDER) || (counted[i] == FillerModel.SHARED_BORDER)) {
                int dist = diagDistance(goal, i);
                if ((dist < closest) && (model.pieces[i] != otherPlayerColour)) {
                    favourite = model.pieces[i];
                    closest = dist;
                }
            }
        }
        return favourite;
    }

    /**
     * Get the most points right now, but win if you can. However, make sure
     * you are getting points your opponent might also get.
     */
    public int smartMostTurn() {
        int attempt = mostIfWinTurn();
        if (attempt < 0) attempt = mostFreeTurn();
        if (attempt < 0) attempt = mostTurn();
        return attempt;
    }

    /**
     * Figure out how many points each colour is worth. If we can win this move,
     * return that colour.
     */
    public int mostIfWinTurn() {
        BitSet b = (BitSet) NO_COLOURS.clone();
        b.set(FillerModel.BORDER);
        b.set(FillerModel.SHARED_BORDER);
        b.set(FillerModel.INTERNAL_BORDER);
        int[] count = countSet(b);
        int favourite = -1;
        int best = -1;
        for (int i=0; i<FillerSettings.NUM_COLOURS; i++) {
            if (count[i] > best) {
                favourite = i;
                best = count[i];
            }
        }
        if (best + score >= FillerSettings.POINTS_TO_WIN) {
            return favourite;
        } else {
            return -1;
        }
    }

    protected BitSet maximise(Evaluator evaluator, BitSet colours, FillerModel model, int[] counted) {
        int most = Integer.MIN_VALUE;
        BitSet answers = (BitSet)NO_COLOURS.clone();
        for (int i=0; i<FillerSettings.NUM_COLOURS; i++) {
            if (colours.get(i)) {
                int val = evaluator.eval(model, counted);
                if (val == most) {
                    answers.set(i);
                } else if (val > most) {
                    answers.and(NO_COLOURS);
                    answers.set(i);
                    most = val;
                }
            }
        }
        return answers;
    }

    public String getIcon() { return "redAlien.gif"; }
}
