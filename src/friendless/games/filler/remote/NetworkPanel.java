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

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.*;
import friendless.awt.HCodeLayout;

/**
 * The panel which lets you configure the options for network play.
 *
 * @author Kris Verbeeck
 */
public class NetworkPanel
    extends JPanel
{
    /** Reference to resources file. */
    private ResourceBundle _resources;

    /** Configuration panel. */
    private JPanel _pConfig;

    /** The Server/Client combo box. */
    private JComboBox _cConType;

    /** Hostname text field. */
    private JTextField _tfHost;

    /** Port text field. */
    private JTextField _tfPort;

    /** Listen/Connect button. */
    private JButton _bConnect;

    /** Chat panel. */
    private JPanel _pChat;

    public NetworkPanel(final ResourceBundle resources) 
    {
        super(new BorderLayout());
        _resources = resources;

        // ===[ Config panel ]===
        _pConfig = new JPanel(new HCodeLayout());
        _pConfig.setBorder(BorderFactory.createTitledBorder("Configuration"));
        _cConType = new JComboBox(new Object[] {"Server", "Client"});
        _cConType.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        String s = (String)e.getItem();
                        if (s.equals("Server")) {
                            _tfHost.setEditable(false);
                            _bConnect.setText("Listen");
                        } else {
                            _tfHost.setEditable(true);
                            _bConnect.setText("Connect");
                        }
                    }
                }
            });
        _pConfig.add("", _cConType);
        _pConfig.add("x", new JPanel());
        _pConfig.add(new JLabel("Host:"));
        _pConfig.add(_tfHost = new JTextField("localhost",10));
        _tfHost.setEditable(false);
        _pConfig.add(new JLabel("Port:"));
        _pConfig.add(_tfPort = new JTextField("4000",5));
        _pConfig.add("x", new JPanel());
        _pConfig.add("", _bConnect = new JButton("Listen"));
        _bConnect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String item = (String)_cConType.getSelectedItem();
                    boolean server = item.equals("Server");
                    String hostname = _tfHost.getText();
                    int port = -1;
                    try {
                        port = Integer.parseInt(_tfPort.getText());
                        RemoteConnection.getInstance().setup(server, hostname, 
                                                             port);
                    } catch (NumberFormatException exc) {
                        exc.printStackTrace();
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                }
            });
        this.add(_pConfig, BorderLayout.NORTH);

        // ===[ Chat panel ]===
        _pChat = new JPanel();
        _pChat.setBorder(BorderFactory.createTitledBorder("Chat"));
        this.add(_pChat, BorderLayout.CENTER);
    }
}
