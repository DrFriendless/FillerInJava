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

import java.io.IOException;

/**
 * The interface of for a remote connection.
 *
 * @author Kris Verbeeck
 */
public interface IsRemoteConnection
{
    /**
     * This method can be used to check whether this Filler is the client
     * or the server in a remote connection.
     * @return True if this Filler instance is the server, false if it is
     *         the client.
     */
    public boolean isServer();

    /**
     * Setup a connection.  Depending on the parameters that are passed
     * to this method, a server or client side connection will be created.
     * @param server True if this is the server side, false for the client
     *               side.
     * @param hostname The hostname to connect to, only used on client side.
     * @param port The port to listen on for the server or to connect to
     *             for the client
     */
    public void setup(boolean server, String hostname, int port)
        throws IOException;
    
    /**
     * A method that will disconnect any clients connected to the server if
     * this is the server side, or disconnect from the remote server if this
     * is the client side.
     */
    public void disconnect() throws IOException;

    /**
     * Can be used to check whether a connection has been established.
     * @return True if a remote connection is present, false otherwise.
     */
    public boolean connected();

    /**
     * Sends a message to the remote party.
     * @param msg The message to be transmitted.
     */
    public void sendMessage(IsMessage msg) throws IOException;

    /**
     * Retrieves a message from the remote party.  Waits until a complete
     * message has arrived.
     * @return The message that has been received.
     */
    public IsMessage receiveMessage() throws IOException;
}
