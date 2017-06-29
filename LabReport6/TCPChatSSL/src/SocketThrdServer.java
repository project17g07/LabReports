import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import javax.net.ssl.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class SocketThrdServer extends JFrame {

	JLabel label = new JLabel("Text received over socket:");
	JPanel panel;
	JTextArea textArea = new JTextArea();
	JScrollPane scrollPane;
	SSLSocket server = null;
	static String address = "127.0.0.1";
	ArrayList<Client> clientList = new ArrayList<Client>();
	final long loginTimeout = 1000000000L;

	/**
	 * Creates a threaded chat server with a window size of 300x400
	 * 
	 * @param args
	 *            unused
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			address = args[0];
		}
		System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "Project17g07");

		SocketThrdServer frame = new SocketThrdServer();
		frame.setTitle("Server Program");
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		frame.addWindowListener(l);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(300, 400);
		frame.listenSocket();
	}

	/**
	 * Constructor
	 */
	SocketThrdServer() {
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		scrollPane = new JScrollPane(textArea);
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.white);
		getContentPane().add(panel);
		panel.add("North", label);
		panel.add("Center", scrollPane);
	}

	/**
	 * Finalization method for exiting the server.
	 */
	protected void finalize() {
		// Objects created in run method are finalized when
		// program terminates and thread exits
		try {
			server.close();
		}
		catch (IOException e) {
			System.out.println("Could not close socket");
			System.exit(-1);
		}
	}

	/**
	 * Waits for clients to start a connection. Creates a new client object with
	 * a unique id. Sends /awaitingLogin when a connection is established. If no
	 * connection is established the Server exits.
	 */
	public void listenSocket() {
		int idGen = 0;
		SSLServerSocket sslserversocket = null;
		try {
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(14444, 99,
					InetAddress.getByName(address));
		}
		catch (SocketException e){
			System.out.println("Error reading keystore file");
			e.printStackTrace();
			System.exit(-1);
		}
		catch (IOException e) {
			System.out.println("Could not listen on port 4444");
			e.printStackTrace();
			System.exit(-1);
		}
		while (true) {
			ClientWorker w;
			try {
				SSLSocket soc = (SSLSocket) sslserversocket.accept();
				clientList.add(new Client(soc, idGen));
				w = new ClientWorker(this, soc, idGen);
				Thread t = new Thread(w);
				t.start();
				sendToClient(getClientFromId(idGen), "/awaitingLogin");
				idGen++;
			}
			catch (IOException e) {
				System.out.println("Accept failed: 4444");
				System.exit(-1);
			}
		}
	}

	/**
	 * Gets a client from a given id.
	 * 
	 * @param id
	 *            The id of a client.
	 * @return Client if found, else null.
	 */
	private Client getClientFromId(int id) {
		for (Client c : clientList) {
			if (c.getId() == id) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Sends a message to all connected AND named clients
	 * 
	 * @param username
	 *            The author of the message
	 * @param text
	 *            The message to be sent
	 */
	private void sendToAll(String username, String text) {
		for (Client c : clientList) {
			if (c.isLoggedIn()) {
				sendToClient(c, username + ": " + text);
			}
		}
	}
	
	/**
	 * Sends a message to all connected AND named clients except for one
	 * 
	 * @param username
	 *            The author of the message
	 * @param text
	 *            The message to be sent
	 * @param clientId
	 *            Id of the Client who shall not receive a message
	 */
	private void sendToAll(String text, int clientId) {
		for (Client c : clientList) {
			if (c.isLoggedIn() && c.getId() != clientId) {
				sendToClient(c, text);
			}
		}
	}
	/**
	 * Sends a given text to a given client over the already established
	 * connection.
	 * 
	 * @param c
	 *            The recieving client
	 * @param text
	 *            The message
	 */
	private void sendToClient(Client c, String text) {
		PrintWriter out;
		try {
			out = new PrintWriter(c.getSocket().getOutputStream(), true);
			out.println(text);
		}
		catch (IOException e) {
			System.out.println("Read failed");
		}
	}

	/**
	 * Interaction between the ClientWorker object and the SocketThrdServer
	 * object. Each message received is processed in this method.
	 * 
	 * @param message
	 *            The message that is received.
	 */
	public void setLatestMessage(String message) {
		Client c = getClientFromId(Integer.parseInt(message.substring(1, message.indexOf(" "))));
		message = message.substring(message.indexOf(" ") + 1, message.length());

		if (message.startsWith("/")) {
			if (message.startsWith("/auth")) {
				String username = checkforDuplicateUsernames(message.split(" ")[1].trim());
				c.setUsername(username);
				textArea.append("User "+ username + " connected" + System.lineSeparator());
				c.setState(true);
				sendToClient(c, "/success");
				sendToAll("User "+ username + " connected", c.getId());
			}
			else {
				textArea.append(message + System.lineSeparator());
			}
		}
		else {
			String username = c.getUsername();
			if (username != null && message.length() > 1) {
				sendToAll(username, message);
				textArea.append(username + ": " + message + System.lineSeparator());
			}
		}
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}
	
	private String checkforDuplicateUsernames(String username)
	{
		for (Client client : clientList) {
			if (client.getUsername()!= null && client.getUsername().equals(username)) {
				username += "1";
				checkforDuplicateUsernames(username);
			}
		}
		return username;
	}
}
