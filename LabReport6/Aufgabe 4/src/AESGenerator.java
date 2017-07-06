import java.math.BigInteger;

public class AESGenerator {

	private int firstCounter = 0;
	private int secondCounter = 1;

	public String getNext(String current) {
		String[] key = current.split("(?<=\\G..)");

		if (firstCounter >= 15) {
			return "";
		}

		if (key[secondCounter].equals("FF")) {
			if (secondCounter >= 15) {
				key[secondCounter] = "00";
				if (key[firstCounter].equals("FF")) {
					key[firstCounter] = "00";
					firstCounter++;
				} else {
					key[firstCounter] = addOneHex(key[firstCounter]);
				}
				secondCounter = firstCounter +1;
			}
			else{
				key[secondCounter] = "00";
				secondCounter++;				
			}
		} else {
			key[secondCounter] = addOneHex(key[secondCounter]);
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < key.length; i++) {
			sb.append(key[i]);
		}
		return sb.toString().toUpperCase();
	}

	private String addOneHex(String hexString) {
		BigInteger i = new BigInteger(hexString, 16);
		i = i.add(BigInteger.ONE);
		String ret = i.toString(16);
		if (ret.length() == 1) {
			ret = "0" + ret;
		}
		return ret;
	}
}