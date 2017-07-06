import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.xml.bind.DatatypeConverter;

public class Program {

	public static HashMap<String, String> _encryptHashMap;
	public static HashMap<String, String> _decryptHashMap;
	public static final String _klartext = "Verschluesselung";
	public static final String _schluesseltext = "be393d39ca4e18f41fa9d88a9d47a574";
	public static long _startingTime;
	
	public static void main(String[] args) {
		_startingTime = System.nanoTime();
		AESGenerator aes = new AESGenerator();
		_encryptHashMap = new HashMap<>();
		_decryptHashMap = new HashMap<>();
		
		String current = "00000000000000000000000000000000";
		
		while (!current.equals("")) {
			encrypt(current);
			decrypt(current);
			current = aes.getNext(current);
		}
		compare();
		_startingTime = System.nanoTime() - _startingTime;
		System.out.println((double)_startingTime / 1000000000.0);
	}

	public static void compare()
	{
		System.out.println("Starting compare RetainAll");
		Set<String> intersection = new HashSet<String>(_encryptHashMap.keySet());
		intersection.retainAll(_decryptHashMap.keySet());
		System.out.println(Arrays.toString(intersection.toArray()));
		System.out.println("Encryption key: " + _encryptHashMap.get(intersection.toArray()[0]));
		System.out.println("Decryption key: " + _decryptHashMap.get(intersection.toArray()[0]));
	}
	
	public static void encrypt(String key) {
		Cipher cipher;
		SecretKeySpec secKey = new SecretKeySpec(DatatypeConverter.parseHexBinary(key), "AES");
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, secKey);
			byte[] encrypted = cipher.doFinal(_klartext.getBytes());
			_encryptHashMap.put(DatatypeConverter.printHexBinary(encrypted),key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void decrypt(String key) {
		Cipher cipher;
		SecretKeySpec secKey = new SecretKeySpec(DatatypeConverter.parseHexBinary(key), "AES");
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, secKey);
			byte[] decrypted = cipher.doFinal(DatatypeConverter.parseHexBinary(_schluesseltext));
			_decryptHashMap.put(DatatypeConverter.printHexBinary(decrypted), key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
