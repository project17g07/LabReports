import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

public class BaziDecrypt {
	
	private byte[] encryptedText;
	private final int REQUIRED_CHARS_FOR_KEY = 3;

	public BaziDecrypt(String filename) throws Exception {
		encryptedText = Files.readAllBytes(new File(filename).toPath());
	}

	public static void main(String[] args) {
		BaziDecrypt baziDecrypt = null;
		if (args.length == 1) {
			try {
				baziDecrypt = new BaziDecrypt(args[0]);
			} catch (Exception e) {
				System.out.println("I/O error when reading file.");
			}
			baziDecrypt.decrypt();
		} else {
			System.out.println("Invalid number of parameters. One parameter required.");
			System.exit(0);
		}
	}
	
	public void decrypt() {
		byte[] key = findKey();
		//byte[] key = {101, 53, 51, 107, 57, 100, 120, 101, 53, 51};
		byte[] plainText = new byte[encryptedText.length];
		for (int i = 0; i < encryptedText.length; i++) {
			plainText[i] = (byte) (key[i % key.length] ^ encryptedText[i]);
		}
		System.out.print("Used key: ");
		for (int i = 0; i < key.length; i++) {
			System.out.print(Integer.toString(key[i]) + " ");
		}
		System.out.print("\n");
		System.out.println(new String(plainText));
	}

	private byte[] findKey() {
		int lastIndex = encryptedText.length - 1;
		byte[] key = null;
		int index = lastIndex;
		List<Byte> tail = new LinkedList<Byte>();
		while (index >= REQUIRED_CHARS_FOR_KEY) {
			((LinkedList<Byte>)(tail)).addFirst(encryptedText[index]);
			boolean possibleCandidate = tail.size() >= REQUIRED_CHARS_FOR_KEY;
			for (int i = 0; possibleCandidate && i < Integer.min(tail.size() - 1, REQUIRED_CHARS_FOR_KEY - 1); i++) {
				possibleCandidate &= encryptedText[lastIndex - i] == encryptedText[index - 1 - i];
			}
			if (possibleCandidate) {
				key = new byte[tail.size()];
				for (int i = 0; i < tail.size(); i++) {
					key[i] = tail.get(i);
				}
			}
			index--;
		}
		return key;
	}

}
