import java.io.*;
import java.net.*;

class ClientThread implements Runnable {
	private Socket client;
	private SocketClient socketClient;

	ClientThread(SocketClient socketClient, Socket client) {
		this.client = client;
		this.socketClient = socketClient;
	}

	public void run() {
		String line;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		}
		catch (IOException e) {
			System.out.println("in or out failed");
			System.exit(-1);
		}

		while (true) {
			try {
				line = in.readLine();
				socketClient.setLatestMessage(line);
			}
			catch (IOException e) {
				System.out.println("Server stopped");
				System.exit(-1);
			}
		}
	}
}
