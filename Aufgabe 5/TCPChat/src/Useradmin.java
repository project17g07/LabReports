import java.io.*;
import java.math.BigInteger;
import java.nio.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class Useradmin implements Useradministration {

	private String pathToFile = "passwort.txt";
	private final boolean ausgabe = true;

	// Commandline launch
	public static void main(String[] args) {
		Useradmin admin = new Useradmin();
		if (args.length > 0) {
			if (args[0].equals("addUser")) {
				char[] pw = System.console().readPassword("Passwort eingeben:");
				admin.addUser(args[1], pw);
				Arrays.fill(pw, '0');
			}
			else if (args[0].equals("checkUser")) {
				char[] pw = System.console().readPassword("Passwort eingeben:");
				admin.checkUser(args[1], pw);
				Arrays.fill(pw, '0');
			}
			else {
				System.out.println("ungültige Parameter");
			}
		}
		else {
			System.out.println("ungültige Parameter");
		}
	}

	// Adds a new User with a given password, the password will be hashed
	public void addUser(String username, char[] password) {
		int random = new Random().nextInt(Integer.MAX_VALUE);
		String salt = byteArrayToString(get_SHA_512_SecurePassword(("" + random).getBytes(), ""));
		String hashedpw = byteArrayToString(hashPassword(password, salt));
		writeToFile(username, salt, hashedpw);
	}

	// Checks if a user with a given password exists
	public boolean checkUser(String username, char[] password) {
		String line;
		Boolean found = false;
		try {
			InputStream fis = new FileInputStream(pathToFile);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				String[] s = line.split(",");
				if (s[0].equals(username)) {
					char[] pw = Arrays.copyOf(password, password.length);
					String hashedpw = byteArrayToString(hashPassword(pw, s[1]));
					if (hashedpw.equals(s[2])) {
						if(ausgabe)System.out.println("Passwort/Benutzerkombination gefunden");
						found = true;
						break;
					}
				}
			}
			if (!found) {
				if(ausgabe)System.out.println("Benutzername / Passwort falsch");
			}
			br.close();
		}
		catch (Exception e) {
			System.out.println("Error: " + e.toString());
			System.exit(0);
		}
		return found;
	}

	// SHA512 Hashingmethod
	private byte[] get_SHA_512_SecurePassword(byte[] passwordToHash, String salt) {
		byte[] passBytes = new byte[passwordToHash.length + salt.getBytes().length];
		System.arraycopy(passwordToHash, 0, passBytes, 0, passwordToHash.length);
		System.arraycopy(salt.getBytes(), 0, passBytes, passwordToHash.length, salt.getBytes().length);
		MessageDigest md;
		byte[] hashedPass = null;
		try {
			md = MessageDigest.getInstance("SHA-512");
			md.update(passBytes);
			hashedPass = new BigInteger(1, md.digest()).toByteArray();
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(0);
		}
		// Fill sensitive arrays with zeroes
		Arrays.fill(passBytes, (byte) 0);
		Arrays.fill(passwordToHash, (byte) 0);
		return hashedPass;
	}

	// Hashes a password 5475 times
	private byte[] hashPassword(char[] password, String salt) {
		byte[] hashedpw = get_SHA_512_SecurePassword(toBytes(password), salt);
		for (int i = 0; i < 5475; i++) {
			hashedpw = get_SHA_512_SecurePassword(hashedpw, salt);
		}
		return hashedpw;
	}

	// Converts a char array to a byte array
	// Used for hashing a password
	// Source:
	// http://stackoverflow.com/questions/30560830/generating-an-md5-hash-with-a-char
	private byte[] toBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
		Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		return bytes;
	}

	private String byteArrayToString(byte[] b) {
		String hex = new BigInteger(1, b).toString(16);
		return hex;
	}

	// Writes username, salt and the hashed password to a file
	// Format username,salt,password
	private void writeToFile(String username, String salt, String password) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(pathToFile, true));
			bw.write(username + "," + salt + "," + password);
			bw.newLine();
			bw.flush();
			bw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}