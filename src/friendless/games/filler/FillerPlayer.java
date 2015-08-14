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

/**
 * An interface to be implemented by something which can play Filler.
 *
 * @author John Farrell
 */
public interface FillerPlayer {
    /** Short string name of player. */
    public String getName();

    /** Fully qualified name of player. */
    public String getFullName();

    /**
     * Tell player where they are starting from.
     * @param origin player's origin
     * @param otherOrigin other player's origin
     */
    public void setOrigin(int origin, int otherOrigin);

    /** Whether this player requires the GUI interface */
    public boolean requiresButtons();

    /**
     * Ask the player to make a move.
     * @param model the board position
     * @param otherPlayerColour the colour the other player has chosen
     */
    public int takeTurn(FillerModel model, int otherPlayerColour);

    /**
     * Inform the player that a colour was chosen using the GUI interface.
     */
    public boolean colourChosen(int c);

    /**
     * @return the name of an icon for this player.
     */
    public String getIcon();
}
