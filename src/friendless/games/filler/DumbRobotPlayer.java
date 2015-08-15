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
 * A base class for robot players which know almost nothing.
 * In particular, these players do not use the model.
 *
 * @author John Farrell
 */
public abstract class DumbRobotPlayer extends AbstractFillerPlayer {
    /** The colour I already am. */
    protected int myColour;
    protected int otherPlayerColour;

    public DumbRobotPlayer() {
        myColour = 0;
    }

    protected BitSet allUsefulColours() {
        BitSet b = allColours();
        if (myColour >= 0) b.clear(myColour);
        if (otherPlayerColour >= 0) b.clear(otherPlayerColour);
        return b;
    }

    /** Choose colours one after another. */
    public int cycleTurn() {
        int favourite = (myColour + 1) % FillerSettings.NUM_COLOURS;
        if (favourite == otherPlayerColour) {
            favourite = (favourite + 1) % FillerSettings.NUM_COLOURS;
        }
        return favourite;
    }

    /** Choose a random colour. */
    public int randomTurn() {
        return chooseRandom(allUsefulColours());
    }

    public String getFullName() { return getClass().getName(); }

    public int takeTurn(FillerModel model, int otherPlayerColour) {
        this.otherPlayerColour = otherPlayerColour;
        myColour = turn();
        if (myColour < 0) {
            myColour = randomTurn();
            System.out.println(getName() + " chooses randomly");
        }
        return myColour;
    }

    /**
     * The robot player has always chosen a colour.
     */
    public boolean colourChosen(int c) { return true; }

    /** The robot player does not require buttons. */
    public boolean requiresButtons() { return false; }

    public String getIcon() { return "robot.png"; }
}
