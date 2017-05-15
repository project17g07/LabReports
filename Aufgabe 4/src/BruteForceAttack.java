public class BruteForceAttack {
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		PasswordCracker cracker;
		if (args.length > 0) {
			cracker = new PasswordCracker(Integer.parseInt(args[0]));
		}
		else
		{
			cracker = new PasswordCracker(0);	
		}
		cracker.bruteForceAttack();
		//for (String pass : cracker.resultArray()) {
			//System.out.println(pass);
		//}		
	}	
}