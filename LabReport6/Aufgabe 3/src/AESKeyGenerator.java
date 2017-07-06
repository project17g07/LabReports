
public class AESKeyGenerator{
	
	private byte value1;
	private byte value2;
	private int pos1;
	private int pos2;
	
	public AESKeyGenerator() {
		reset();
	}
	
	public void reset() {
		value1 = Byte.MIN_VALUE;
		value2 = Byte.MIN_VALUE;
		pos1 = 0;
		pos2 = 0;
	}
	
	public boolean hasNext() {
		return value1 < Byte.MAX_VALUE || value2 < Byte.MAX_VALUE || pos1 < 14;
	}
	
	public byte[] nextKey() {
		if (pos2 < 15) {
			pos2++;
		} else if (pos1 < 14) {
			pos1++;
			pos2 = pos1 + 1;
		} else if (value2 < Byte.MAX_VALUE) {
			pos1 = 0;
			pos2 = 1;
			value2++;
		} else {
			value1++;
			System.out.println(value1);
			value2 = Byte.MIN_VALUE;
			pos1 = 0;
			pos2 = 1;
		}
		byte[] key = new byte[16];
		key[pos1] = value1;
		key[pos2] = value2;
		return key;
	}
}
