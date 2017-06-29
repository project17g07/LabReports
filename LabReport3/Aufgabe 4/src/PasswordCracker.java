import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PasswordCracker {

	private int _startingPoint;
	private TCPSend _tcpSend;

	/**
	 * 
	 * @param hash
	 * @param salt
	 */
	public PasswordCracker(int startingPoint) {
		_startingPoint = startingPoint;
		_tcpSend = new TCPSend();
	}

	/**
	 * 
	 * @param candidate
	 * @return
	 */
	private void test(int i) {
		String candidate = "" + i;
		while (candidate.length() < 8) {
			candidate = "0" + candidate;
		}
		String send = "SERIAL=" + candidate;
		_tcpSend.sendLine(send);
		String s = _tcpSend.readLine();
		System.out.println(s);
		if (s.startsWith("SERIAL_VALID=1")) {
			writeToFile(candidate);
		} else if (s.startsWith("SERIAL_VALID=0")) {
			if (s.contains("invalid length") || s.contains("help")) {
				test(i);
			}
		}
	}

	/**
	 * Tries all combinations of lowercase letters and numbers upto a length of
	 * 6 characters.
	 * 
	 * @return password or null if password could not be found
	 */
	public void bruteForceAttack() {
		int candidate = _startingPoint;
		while (candidate <= 99999999) {
			test(candidate);
			candidate++;
		}
	}
	
	public List<String> resultArray()
	{
		return null;//_passwordList;
	}
	
	private void writeToFile(String password)
	{
		try {
			password = password.substring(0, password.length() -1) +"0";
			BufferedWriter bw = new BufferedWriter(new FileWriter("keys.txt", true));
			bw.write(password);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
