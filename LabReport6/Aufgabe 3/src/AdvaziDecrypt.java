import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AdvaziDecrypt {
	
	private byte[] encryptedText;
	private final int MINIMUM_KEY_LENGTH = 3;

	public AdvaziDecrypt(String filename) throws IOException {
		encryptedText = Files.readAllBytes(new File(filename).toPath());
	}

	public static void main(String[] args) {
		AdvaziDecrypt advaziDecrypt = null;
		if (args.length == 1) {
			try {
				advaziDecrypt = new AdvaziDecrypt(args[0]);
			} catch (IOException e) {
				System.out.println("I/O error when reading file.");
				System.exit(0);
			}
			advaziDecrypt.decrypt();
		} else {
			System.out.println("Invalid number of parameters. One parameter required.");
			System.exit(0);
		}
	}
	
	public void decrypt() {
		byte[] key = findKey();
		int numberOfPaddingBytes = countPaddingBytes(key);
		for (int i = 0; i < key.length; i++) {
			key[i] = (byte)(key[i] ^ numberOfPaddingBytes);
		}
		byte[] plainText = new byte[encryptedText.length];
		for (int i = 0; i < encryptedText.length; i++) {
			plainText[i] = (byte) (key[i % key.length] ^ encryptedText[i]);
		}
		System.out.print("Used key: ");
		for (int i = 0; i < key.length; i++) {
			System.out.print(Integer.toString(key[i]) + " ");
		}
		System.out.print("\n");
		System.out.println(new String(Arrays.copyOf(plainText, plainText.length - numberOfPaddingBytes)));
	}

	private int countPaddingBytes(byte[] key) {
		boolean isPaddingByte = true;
		int i = 0;
		int index = 0;
		while (isPaddingByte && i < encryptedText.length) {
			index = (key.length - 1 - i) % key.length;
			if (index < 0) {
				index += key.length;
			}
			isPaddingByte &= key[index] == encryptedText[encryptedText.length - 1 - i];
			i++;
		}
		return i - 1;
	}

	private byte[] findKey() {
		int lastIndex = encryptedText.length - 1;
		byte[] key = null;
		int index = lastIndex;
		List<Byte> tail = new LinkedList<Byte>();
		while (index > lastIndex / 2) {
			((LinkedList<Byte>)(tail)).addFirst(encryptedText[index]);
			boolean possibleCandidate = tail.size() >= MINIMUM_KEY_LENGTH;
			for (int i = 0; possibleCandidate && i < tail.size(); i++) {
				possibleCandidate &= encryptedText[lastIndex - i] == encryptedText[index - 1 - i];
			}
			if (possibleCandidate) {
				key = new byte[tail.size()];
				for (int i = 0; i < tail.size(); i++) {
					key[i] = tail.get(i);
				}
				break;
			}
			index--;
		}
		return key;
	}

}
