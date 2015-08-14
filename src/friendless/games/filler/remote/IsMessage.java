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

package friendless.games.filler.remote;

/**
 * This interface will have to be implemented by every message that
 * is send to a remote instance of Filler.
 * <p>
 * A message will be send from a client to a server (or vice versa)
 * through the use of TCP sockets.  Every raw message will consist
 * of a series of bytes of which the first byte identifies the type
 * of the message (see the getMessageId method).
 *
 * @author Kris Verbeeck
 */
public interface IsMessage
{
    /**
     * This method can be used to initialize a message instance through
     * the use of a payload that has been received over a connecton.
     * @param payload The received bytes.
     */
    public void init(byte[] payload);
 
    /**
     * Returns a unique message id for this type of message.  This
     * is the first byte that will be send over the connection making
     * it possible for the remote party to identify the type of 
     * message and the size of its payload before having read the
     * complete message.
     * @return The unique message ID.
     */
    public byte getMessageId();

    /**
     * This method is used by the sending party to get all the payload 
     * data from a message before sending it.  The message ID will
     * be prepended automatically.
     * @return A byte array containing the message's payload.
     */
    public byte[] getPayload();

    /**
     * This method is called when the receiving party needs to know
     * how big the message's payload is.  If the size is unknown then
     * a sentinel will be used.
     * @see #getSentinel()
     * @return The size of the message payload (in bytes) or -1 if
     *         it is unknown.
     */
    public int getPayloadSize();

    /**
     * This method will have to return the byte that acts as a sentinel
     * for messages that dont' have a fixed length.  This method is
     * only called by the receiving party when the getPayLoadSize method
     * returns -1.
     * @see #getPayloadSize()
     * @return The byte that acts as a sentinel.
     */
    public byte getSentinel();
}
