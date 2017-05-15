import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
	ServerSocket server = null;
	ArrayList<Client> clientList = new ArrayList<Client>();
	Useradmin admin = new Useradmin();
	final long loginTimeout = 1000000000L;

	/**
	 * Creates a threaded chat server with a window size of 300x400
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {
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
		try {
			server = new ServerSocket(4444);
		}
		catch (IOException e) {
			System.out.println("Could not listen on port 4444");
			System.exit(-1);
		}
		while (true) {
			ClientWorker w;
			try {
				Socket soc = server.accept();
				clientList.add(new Client(soc, idGen));
				w = new ClientWorker(this, soc, idGen);
				Thread t = new Thread(w);
				t.start();
				printTextToClient(getClientFromId(idGen), "/awaitingLogin");
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
	 * @param id The id of a client.
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
	 * Sends a message to all connected AND authenticated clients
	 * 
	 * @param username The author of the message
	 * @param text The message to be sent
	 */
	private void sendToAll(String username, String text) {
		for (Client c : clientList) {
			if (c.isLoggedIn()) {
				printTextToClient(c, username + ": " + text);
			}
		}
	}

	/**
	 * Sends a given text to a given client over the already established
	 * connection.
	 * 
	 * @param c The recieving client
	 * @param text The message
	 */
	private void printTextToClient(Client c, String text) {
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
	 * Checks if a client is allowed to login. Compares the current systemtime
	 * to the last logintime of the given client. If the difference is higher
	 * than the given login timeout no login will be possible.
	 * A check if the user is already authenticated is missing.
	 * 
	 * @param c The client who is checked
	 * @return true if allowed, else false.
	 */
	private boolean allowLogin(Client c) {
		long timeNow = System.nanoTime();
		long lastLoginTime = timeNow - c.getLastLoginAttempt();
		if (lastLoginTime > loginTimeout) {
			return true;
		}
		else {
			int nextLogin = (int) ((loginTimeout - lastLoginTime) / 1000000000);
			printTextToClient(c, "/failed " + nextLogin);
			return false;
		}
	}

	/**
	 * Authenticates a given client. Checks the supplied credentials against the
	 * saved credentials in a file. On success it sets the loggedIn property to
	 * true.
	 * 
	 * @param c The client that needs checking
	 * @param message The credential string
	 */
	private void authUser(Client c, String message) {
		if (c == null) {
			return;
		}

		if (message.startsWith("/auth")) {
			if (!allowLogin(c)) {
				return;
			}
			c.setLastLoginAttempt(System.nanoTime());
			String[] up = message.split(" ");
			boolean found = admin.checkUser(up[1], up[2].toCharArray());
			if (found) {
				c.setState(true);
				c.setUsername(up[1]);
				printTextToClient(c, "/success");
			}
			else {
				allowLogin(c);
			}
		}
	}

	/**
	 * Interaction between the ClientWorker object and the SocketThrdServer
	 * object. Each message received is processed in this method.
	 * 
	 * @param message The message that is received.
	 */
	public void setLatestMessage(String message) {
		Client c = getClientFromId(Integer.parseInt(message.substring(1, message.indexOf(" "))));
		String username = c.getUsername();
		message = message.substring(message.indexOf(" ") + 1, message.length());

		if (message.startsWith("/")) {
			if (message.startsWith("/auth")) {
				textArea.append(message.substring(0, message.lastIndexOf(" ")) + " *****" + System.lineSeparator());
				authUser(c, message);
			}
			else if(message.equals("/retry")){
				printTextToClient(c, "/failed 0");
			}
			else {
				textArea.append(message + System.lineSeparator());
			}
		}
		else {
			if (username != null && message.length() > 1) {
				sendToAll(username, message);
				textArea.append(username + ": " + message + System.lineSeparator());
			}
		}
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}
}
