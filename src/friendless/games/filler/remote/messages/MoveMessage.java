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

package friendless.games.filler.remote.messages;

import friendless.games.filler.remote.FixedSizeMessage;
import friendless.games.filler.remote.IsMessageID;

/**
 * A message that contains one move.
 *
 * @author Kris Verbeeck
 */
public class MoveMessage
    extends FixedSizeMessage
    implements IsMessageID
{
    private byte[] _data;

    /**
     * Create a new move message.  This is used by the RemoteConnection
     * class to create an unitialized message when a message of this
     * type arrives.  The init method will be called to fill in the data.
     */
    public MoveMessage()
    {
        super(MSGID_GAME_MOVE, 1); // Message size is 1.
    }

    /**
     * Create a new move message.
     * @param color The color that was selected in this move.
     */
    public MoveMessage(int color)
    {
        super(MSGID_GAME_MOVE, 1); // Message size is 1.
        _data = new byte[] {(byte)color};
    }
    
    /**
     * Initialize the data by getting the first byte from the payload.
     * @param payload The received bytes.
     */
    public void init(byte[] payload)
    {
        _data = new byte[] {payload[0]};
    }
    

    /**
     * The only data in this message is the color of this move.
     */
    public byte[] getPayload()
    {
        return _data;
    }

    /**
     * Returns a nice representation of a move message.
     */
    public String toString()
    {
        return "Move = "+_data[0];
    }
}
