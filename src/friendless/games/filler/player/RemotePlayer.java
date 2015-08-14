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

import java.io.IOException;

import friendless.games.filler.AbstractFillerPlayer;
import friendless.games.filler.FillerModel;
import friendless.games.filler.remote.RemoteConnection;
import friendless.games.filler.remote.IsMessage;
import friendless.games.filler.remote.IsMessageID;

/**
 * A player controlled from the GUI.
 *
 * @author John Farrell
 */
public class RemotePlayer 
    extends AbstractFillerPlayer
{

    public String getName() { return "Remote"; }

    public String getFullName() { return getName(); }

    public boolean colourChosen(int c) { return true; }

    public boolean requiresButtons() { return false; }

    public int takeTurn(FillerModel model, int otherPlayerColour)
    {
        return turn();
    }

    public int turn()
    {
        try {
            IsMessage msg = RemoteConnection.getInstance().receiveMessage();
            if (msg.getMessageId() == IsMessageID.MSGID_GAME_MOVE) {
                System.out.println("Remote player selecting: "+msg.getPayload()[0]);
                return msg.getPayload()[0];
            } else {
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
