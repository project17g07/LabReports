import java.io.*;
import java.net.*;

class ClientWorker implements Runnable {
	private Socket client;
	private SocketThrdServer socketThrdServer;
	private int id;
	private boolean connected = false;

	ClientWorker(SocketThrdServer socketThrdServer, Socket client, int id) {
		this.client = client;
		this.socketThrdServer = socketThrdServer;
		this.id = id;
		connected = true;
	}

	public void run() {
		String line;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			System.out.println("in or out failed");
			System.exit(-1); 
		}

		while (connected) {
			try {
				line = in.readLine();
				socketThrdServer.setLatestMessage("/" + id + " " + line);
			} catch (IOException e) {
				System.out.println("Client "+ id+ " disconnected");
				socketThrdServer.setLatestMessage("/" + id + " disconnected");
				connected = false;
			}
		}
	}
}
