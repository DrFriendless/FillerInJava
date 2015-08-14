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

package friendless.games.filler.player;

import friendless.games.filler.*;

/**
 * A player controlled from the GUI.
 *
 * @author John Farrell
 */
public class HumanFillerPlayer extends AbstractFillerPlayer {
    protected int otherPlayerColour;
    protected int chosenColour;

    public String getName() { return "Human"; }

    public String getFullName() { return getName(); }

    public boolean requiresButtons() { return true; }

    public int turn() {
        while (true) {
            synchronized (this) {
                try {
                    wait();
                    break;
                } catch (InterruptedException ie) {
                    continue;
                }
            }
        }
        return chosenColour;
    }

    public boolean colourChosen(int c) {
        if (c == otherPlayerColour) return false;
        chosenColour = c;
        synchronized (this) { notifyAll(); }
        return true;
    }

    public final int takeTurn(FillerModel model, int otherPlayerColour) {
        this.otherPlayerColour = otherPlayerColour;
        return turn();
    }
}
