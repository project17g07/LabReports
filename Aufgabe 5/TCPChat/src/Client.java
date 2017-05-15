import java.net.Socket;

public class Client {

	private final Socket _socket;
	private boolean _loggedIn;
	private final int _id;
	private String _username;
	private long _lastLoginAttempt;

	public Client(Socket socket, int id) {
		_socket = socket;
		_loggedIn = false;
		_id = id;
		_lastLoginAttempt = 0L;
	}

	public Socket getSocket() {
		return _socket;
	}

	public int getId() {
		return _id;
	}

	public void setLastLoginAttempt(long time) {
		_lastLoginAttempt = time;
	}

	public long getLastLoginAttempt() {
		return _lastLoginAttempt;
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
