import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


public class Useradmin implements Useradministration {
	public String get_SHA_512_SecurePassword(String passwordToHash, String   salt){
		String generatedPassword = null;
		    try {
		         MessageDigest md = MessageDigest.getInstance("SHA-512");
		         md.update(salt.getBytes("UTF-8"));
		         byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
		         StringBuilder sb = new StringBuilder();
		         for(int i=0; i< bytes.length ;i++){
		            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		         }
		         generatedPassword = sb.toString();
		        } 
		       catch (NoSuchAlgorithmException e){
		        e.printStackTrace();
		       } catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return generatedPassword;
		}
	

	public void addUser(String username, char[] password){
		Random random = new Random();
		String salt = get_SHA_512_SecurePassword(Integer.toString(random.nextInt()), "");
		String pw = new String(password);
		for (int i = 0; i < 1000; i++) {
			pw = get_SHA_512_SecurePassword(pw, salt);
		}
	}

	public boolean checkUser(String username, char[] password) {
		// TODO Auto-generated method stub
		return false;
	}

}
