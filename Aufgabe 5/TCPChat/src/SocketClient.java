import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

class SocketClient extends JFrame implements ActionListener {

	JLabel text, clicked;
	JButton button;
	JPanel panel;
	JPanel innerPanel;
	JTextField textField;
	JTextArea textArea;
	JScrollPane scrollPane;
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;

	/**
	 * Creates a threaded chat window with a window size of 300x400
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {
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
	} // End Constructor

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
			socket = new Socket("localhost", 4444);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			ClientThread cT = new ClientThread(this, socket);
			Thread t = new Thread(cT);
			t.start();
		}
		catch (UnknownHostException e) {
			System.out.println("Unknown host: kq6py.eng");
			System.exit(1);
		}
		catch (IOException e) {
			System.out.println("No I/O");
			System.exit(1);
		}
	}

	/**
	 * Creates a modal dialogue that asks for a username and a password. On pressing OK in the dialogue
	 * it will send the credentials to the server to verify with the server. On pressing to closing 
	 * button the application will exit.
	 * @return the authentication string containing username and password
	 */
	private char[] dialogue() {
		JTextField username = new JTextField();
		JPasswordField password = new JPasswordField();
		final JComponent[] inputs = new JComponent[] { new JLabel("Username"), username, new JLabel("Password"),
				password };
		int result = JOptionPane.showConfirmDialog(null, inputs, "Credentials", JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			if(username.getText().length() <= 0 || password.getPassword().length <= 0)
			{
				return "/retry".toCharArray();
			}
			username.setText("/auth " + username.getText() + " ");
			char[] up = new char[username.getText().toCharArray().length + password.getPassword().length];
			System.arraycopy(username.getText().toCharArray(), 0, up, 0, username.getText().toCharArray().length);
			System.arraycopy(password.getPassword(), 0, up, username.getText().toCharArray().length,
					password.getPassword().length);
			return up;
		}else if (result == JOptionPane.CLOSED_OPTION)
		{
			System.exit(1);
		}
		return "/retry".toCharArray();
	}

	/**
	 * Interaction between the ClientThread object and the SocketClient
	 * object. Each message received from the server is processed in this method.
	 * 
	 * @param message The message that is received.
	 */
	public void setLatestMessage(String message) {
		if (message.equals("/awaitingLogin")) {
			textArea.setText("Please login." + System.lineSeparator());
			char[] up = dialogue();
			out.println(up);
		}
		else if(message.equals("/success")){
			textArea.append("Login successful." + System.lineSeparator());
		}
		else if (message.startsWith("/failed")) {
			try {
				int seconds = Integer.parseInt(message.substring(8, message.length()));
				int wait = seconds;
				textArea.setText("Login failed. Please try again in " + seconds + " seconds." + System.lineSeparator());
				for (int i = 0; i < seconds; i++) {
					TimeUnit.SECONDS.sleep(1);
					wait -= 1;
					textArea.setText("Login failed. Please try again in " + wait + " seconds."
							+ System.lineSeparator());
				}
				textArea.setText("Login failed. Please try again."
						+ System.lineSeparator());
				char[] up = dialogue();
				out.println(up);
			}
			catch (InterruptedException e) {
			}
		}
		else {
			textArea.append(message + System.lineSeparator());
		}
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}
}
