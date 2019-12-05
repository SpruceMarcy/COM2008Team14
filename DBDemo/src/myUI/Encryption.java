package myUI;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class Encryption {
	
	private static void main(String[] args) {
		String mypw= "uos";
		String encrytedPW = generateHash(mypw);
		String mypw2= "uos";
		String encrytedPW2 = generateHash(mypw);
		System.out.println(comparePasswords("uos",encrytedPW));
		System.out.println(encrytedPW.equals(encrytedPW2));
	}
	
	public static Boolean comparePasswords(String rawPassword, String hashedPassword) {
		String salt = hashedPassword.substring(0,24);
		System.out.println(salt+encrypt(rawPassword,salt));
		return (salt+encrypt(rawPassword,salt)).equals(hashedPassword);
	}
	
	public static String generateHash(String password) {
		String resultingHash = null;
		String salt = newSalt();
		
		resultingHash=salt + encrypt(password,salt);
		
        return resultingHash;
	}
	
	private static String newSalt() {
		byte[] salt = new byte[16];
		new SecureRandom().nextBytes(salt);
		return new String(Base64.getEncoder().encode(salt), StandardCharsets.UTF_8);
	}

	private static String encrypt(String message, String salt) {
		String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((salt+message).getBytes());
            result=new String(Base64.getEncoder().encode(hash), StandardCharsets.UTF_8);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return result;
	}
}
