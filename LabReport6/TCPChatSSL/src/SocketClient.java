import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;

import java.io.*;
import java.net.*;

class SocketClient extends JFrame implements ActionListener {

	JLabel text, clicked;
	JButton button;
	JPanel panel;
	JPanel innerPanel;
	JTextField textField;
	JTextArea textArea;
	JScrollPane scrollPane;
	SSLSocket sslsocket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	static String address = "127.0.0.1";

	/**
	 * Creates a threaded chat window with a window size of 300x400
	 * 
	 * @param args
	 *            unused
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			address = args[0];
		}
		
		System.setProperty("javax.net.ssl.trustStore", "truststore.ts");
		System.setProperty("javax.net.ssl.keyStorePassword", "Project17g07");

		SocketClient frame = new SocketClient();
		frame.setTitle("Client Program");
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
	SocketClient() {
		text = new JLabel("Text to send over socket:");
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textField = new JTextField(20);
		scrollPane = new JScrollPane(textArea);
		button = new JButton("Click Me");
		button.addActionListener(this);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.white);
		innerPanel = new JPanel();
		innerPanel.setLayout(new BorderLayout());
		innerPanel.add("Center", scrollPane);
		innerPanel.add("North", textField);
		panel.add("Center", innerPanel);
		getContentPane().add(panel);
		panel.add("North", text);
		panel.add("South", button);
	}

	/**
	 * On button press it sends the contents of the textbox to the server.
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == button) {
			String text = textField.getText();
			out.println(text);
			textField.setText(new String(""));
		}
	}

	/**
	 * Creates a connection to the chatserver. Starts a thread to listen to the
	 * incoming messages. Will exit on non successful connection.
	 */
	public void listenSocket() {
		try {
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			sslsocket = (SSLSocket) sslsocketfactory.createSocket(address, 14444);
			out = new PrintWriter(sslsocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
			ClientThread cT = new ClientThread(this, sslsocket);
			Thread t = new Thread(cT);
			t.start();
		}
		catch (UnknownHostException e) {
			System.out.println("Unknown host: " + address);
			System.exit(1);
		}
		catch (IOException e) {
			System.out.println("No I/O");
			System.exit(1);
		}
	}

	/**
	 * Creates a modal dialogue that asks for a username and a password. On
	 * pressing OK in the dialogue it will send the credentials to the server to
	 * verify with the server. On pressing to closing button the application
	 * will exit.
	 * 
	 * @return the authentication string containing username and password
	 */
	private String dialogue() {
		JTextField username = new JTextField();
		final JComponent[] inputs = new JComponent[] { new JLabel("Username"), username };
		int result = JOptionPane.showConfirmDialog(this, inputs, "Select a Username", JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			if (username.getText().length() <= 0) {
				return "/retry";
			}
			username.setText("/auth " + username.getText());
			return username.getText();
		}
		else if (result == JOptionPane.CLOSED_OPTION) {
			System.exit(1);
		}
		return "/retry";
	}

	/**
	 * Interaction between the ClientThread object and the SocketClient object.
	 * Each message received from the server is processed in this method.
	 * 
	 * @param message
	 *            The message that is received.
	 */
	public void setLatestMessage(String message) {
		if (message.equals("/awaitingLogin")) {
			textArea.setText("Please select a username." + System.lineSeparator());
			out.println(dialogue());
		}
		else if (message.equals("/success")) {
			textArea.append("Welcome to the ProjectNetsec Chatroom!." + System.lineSeparator());
		}
		else {
			textArea.append(message + System.lineSeparator());
		}
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}
}
