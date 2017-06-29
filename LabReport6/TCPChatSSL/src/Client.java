import javax.net.ssl.SSLSocket;

public class Client {

	private final SSLSocket _socket;
	private boolean _loggedIn;
	private final int _id;
	private String _username;

	public Client(SSLSocket socket, int id) {
		_socket = socket;
		_loggedIn = false;
		_id = id;
	}

	public SSLSocket getSocket() {
		return _socket;
	}

	public int getId() {
		return _id;
	}

	public void setUsername(String username) {
		_username = username;
	}

	public String getUsername() {
		return _username;
	}

	public void setState(boolean loggedIn) {
		_loggedIn = loggedIn;
	}

	public boolean isLoggedIn() {
		return _loggedIn;
	}
}
