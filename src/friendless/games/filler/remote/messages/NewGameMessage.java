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

import friendless.games.filler.FillerModel;
import friendless.games.filler.remote.FixedSizeMessage;
import friendless.games.filler.remote.IsMessageID;

/**
 * A message to send at the start of a new game.  This message contains
 * the board position.  The board position is encoded into 689 bytes, every
 * byte contains two consecutive colors (except for the last byte).
 *
 * @author Kris Verbeeck
 */
public class NewGameMessage
    extends FixedSizeMessage
    implements IsMessageID
{
    private byte[] _data;
    private int[] _pieces;

    /**
     * Create a new game message.  This is used by the RemoteConnection
     * class to create an unitialized message when a message of this
     * type arrives.  The init method will be called to fill in the data.
     */
    public NewGameMessage()
    {
        super(MSGID_GAME_NEW, 713);
    }

    /**
     * Create a new game message.
     * @param p The board layout.
     */
    public NewGameMessage(int[] p)
    {
        super(MSGID_GAME_NEW, 713);
        _data = new byte[713];
        for (int i=0; i<_data.length-1; i++) {
            _data[i] = encode(p[2*i], p[2*i+1]);
        }
        _data[712] = encode(p[1424], 0);
    }

    /**
     * Encode two pieces into one byte.
     */
    private byte encode(int i1, int i2)
    {
        int hi = (i1 == -1 ? 0x0F : i1) << 4;
        int lo = (i2 == -1 ? 0x0F : i2);
        System.out.println("Encode "+i1+" and "+i2+" into "+((byte)(hi+lo)));
        return (byte)(hi + lo);
    }

    /**
     * Decode on byte into two pieces.
     */
    private int[] decode(byte b)
    {
        int i = (int)(b < 0 ? b+256 : b);
        int hi = i >> 4;
        int lo = i & 0x0F;
        hi = (hi == 0x0F ? -1 : hi);
        lo = (lo == 0x0F ? -1 : lo);
        System.out.println("Decode "+b+" into "+hi+" and "+lo);
        return new int[] {hi, lo};
    }

    /**
     * Initialize the data by decoding it into a pieces array.
     * @param payload The received bytes.
     */
    public void init(byte[] payload)
    {
        int hilo[];
        _data = (byte[])payload.clone();
        _pieces = new int[1425];
        for (int i=0; i<_data.length-1; i++) {
            hilo = decode(payload[i]);
            _pieces[2*i] = hilo[0];
            _pieces[2*i+1] = hilo[1];
        }
        hilo = decode(payload[712]);
        _pieces[1424] = hilo[0];
    }
    

    /**
     * The only data in this message is the color of this move.
     */
    public byte[] getPayload()
    {
        return _data;
    }

    /**
     * Returns the array of pieces encoded by this message.
     */
    public int[] getPieces()
    {
        return _pieces;
    }

    /**
     * Returns a nice representation of a move message.
     */
    public String toString()
    {
        return "New game";
    }
}
