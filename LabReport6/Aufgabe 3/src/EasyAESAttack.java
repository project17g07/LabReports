import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EasyAESAttack {

	/*
	 * Schlüssel1: 000000f5000000000000630000000000
	 * Schlüssel2: 0000000077000000b000000000000000
	 */
	private final String plainText = "Verschluesselung";
	private final String encryptedHexString = "be393d39ca4e18f41fa9d88a9d47a574";
	private AESKeyGenerator keyGenerator = new AESKeyGenerator();
	private Map<String, byte[]> keyStore = new HashMap<String, byte[]>();

	public static void main(String[] args) {
		EasyAESAttack attack = new EasyAESAttack();
		attack.bruteForceEncrypt();
		attack.bruteForceDecrypt(attack.encryptedHexString);
	}

	private static byte[] hexStringToBytes(String hexString) {
		byte[] bytes = new byte[hexString.length() / 2];
		for (int i = 0; i < hexString.length() / 2; i++) {
			bytes[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
		}
		return bytes;
	}

	private static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		int a = 0;
		for (int i = 0; i < bytes.length; i++) {
			a = bytes[i] < 0 ? 256 + bytes[i] : bytes[i];
			if (a < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(a));
		}
		return sb.toString();
	}

	private void bruteForceDecrypt(String encryptedText) {
		keyGenerator.reset();
		String candidate = "";
		byte[] encryptedTextAsBytes = hexStringToBytes(encryptedText);
		byte[] key2 = null;
		while (keyGenerator.hasNext() && !keyStore.containsKey(candidate)) {
			key2 = keyGenerator.nextKey();
			try {
				candidate = bytesToHexString(decryptData(encryptedTextAsBytes, key2));
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		byte[] key1 = keyStore.get(candidate);
		System.out.println("Schlüssel1: " + bytesToHexString(key1));
		System.out.println("Schlüssel2: " + bytesToHexString(key2));
		try {
			System.out.println("Test: " + bytesToHexString(encryptData(encryptData(plainText.getBytes(), key1), key2)));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void bruteForceEncrypt() {
		byte[] plainTextAsBytes = plainText.getBytes();
		keyGenerator.reset();
		byte [] key = null;
		while (keyGenerator.hasNext()) {
			key = keyGenerator.nextKey();
			try {
				keyStore.put(bytesToHexString(encryptData(plainTextAsBytes, key)), key);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	/*
	 * Funktionen decryptData() und encryptData() entnommen von:
	 * https://stackoverflow.com/questions/22034269/encryption-decryption-using-
	 * aes-ecb-nopadding
	 */
	private byte[] decryptData(byte[] encData, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		return cipher.doFinal(encData);
	}

	private byte[] encryptData(byte[] data, byte[] key) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		return cipher.doFinal(data);
	}
}
