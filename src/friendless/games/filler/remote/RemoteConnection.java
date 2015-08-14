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

import java.net.*;
import java.io.*;

/**
 * An abstract class for playing against remote opponents.
 *
 * @author Kris Verbeeck
 */
public class RemoteConnection
    implements IsRemoteConnection
{
    // Singleton reference to the connection
    private static IsRemoteConnection _connection = null;

    /**
     * The message classes.  Make sure that the message are order 
     * correctly; msgs[msg_id] should point to the correct message. 
     * Every string in this array is prepended with the package name 
     * <code>friendless.games.filler.remote.messages</code> and also
     * the string <code>Message</code> is appended to it.  
     * E.g.: <code>Move</code> points to the class
     * <code>friendless.games.filler.remote.messages.MoveMessage</code>.
     */
    private static String[] _msgNames = {"NewGame", "Move"};

    // The sockets used for communication.
    private ServerSocket _server;
    private Socket _client;

    // The streams used for communicating
    private InputStream _in;
    private OutputStream _out;

    /**
     * Private constructor because this is a singleton class.
     */
    private RemoteConnection()
    {
        _server = null;
        _client = null;
        _in = null;
        _out = null;
    }

    /**
     * Get a reference to the RemoteConnection singleton.
     */
    public static IsRemoteConnection getInstance()
    {
        if (_connection == null) {
            _connection = new RemoteConnection();
        }
        return _connection;
    }

    /**
     * Returns true/false depending on whether this is the server
     * or client side of the connection.
     */
    public boolean isServer()
    {
        return (_server != null);
    }
    
    /**
     * Setup a connection.  Depending on the parameters that are passed
     * to this method, a server or client side connection will be created.
     * <ul>
     *   <li><b>Server:</b>&nbsp;&nbsp; If no connection has been established 
     *                     yet, a new server connection will be created and
     *                     the listening for incomming client connections
     *                     will be started.  If there is already a server
     *                     connection then any previous client that might be 
     *                     connected will get disconnected before we start
     *                     listening again for a new client.  If there
     *                     is already a client connection then we will first
     *                     terminate that before setting up a server 
     *                     connection.</li>
     *   <li><b>Client:</b>&nbsp;&nbsp; If there is not yet a connection
     *                     then a new connection will be created.  If there
     *                     is already a connection then that connection will
     *                     first be closed before establishing a new one.</li>
     * </ul>
     * @param server True if this is the server side, false for the client
     *               side.
     * @param hostname The hostname to connect to, only used on client side.
     * @param port The port to listen on for the server or to connect to
     *             for the client
     */
    public void setup(boolean server, String hostname, int port)
        throws IOException
    {
        disconnect();
        if (server) {
            // Setup server connection
            System.out.println("Server on port "+port);
            if (_server == null) {
                _server = new ServerSocket(port, 1);
            }
            System.out.println("Waiting for connection...");
            _client = _server.accept(); 
            System.out.println("Connection arrived...");
        } else {
            // Setup client connection
            System.out.println("Client, connecting to "+hostname+":"+port);
            _client = new Socket(hostname, port);
            System.out.println("Connection made...");
        }
        _in = _client.getInputStream();
        _out = _client.getOutputStream();
    }

    /**
     * A method that will disconnect any clients connected to the server if
     * this is the server side, or disconnect from the remote server if this
     * is the client side.
     */
    public void disconnect()
        throws IOException
    {
        if (_client != null) _client.close();
        _client = null;
    }

    /**
     * Can be used to check whether a connection has been established.
     * @return True if a remote connection is present, false otherwise.
     */
    public boolean connected()
    {
        return (_in != null);
    }

    /**
     * Sends a message to the remote party.
     * @param msg The message to be transmitted.
     */
    public void sendMessage(IsMessage msg)
        throws IOException
    {
        System.out.println("Sending message: "+msg);
        _out.write(msg.getMessageId());
        _out.write(msg.getPayload());
        _out.flush();
    }

    /**
     * Retrieves a message from the remote party.  Waits until a complete
     * message has arrived.
     * @return The message that has been received.
     */
    public IsMessage receiveMessage()
        throws IOException
    {
        byte id = (byte)_in.read();
        try {
            Class clazz = 
                Class.forName("friendless.games.filler.remote.messages."+
                              _msgNames[id]+"Message");
            IsMessage msg = (IsMessage)clazz.newInstance();
            int size = msg.getPayloadSize();
            if (size != -1) {
                // fixed size
                byte[] payload = new byte[size];
                int offset = 0, read = 0;
                while ((read != -1) && (size > 0)) {
                    read = _in.read(payload, offset, size);
                    size -= read;
                    System.out.println("Read "+read+"/"+payload.length+
                                       " of payload");
                }
                if (size == 0) {
                    // only if we were able to read to complete payload
                    msg.init(payload);
                }
            } else {
                // use sentinel
            }
            System.out.println("Received message: "+msg);
            return msg;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
