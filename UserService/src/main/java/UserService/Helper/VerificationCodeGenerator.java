package UserService.Helper;

import java.security.SecureRandom;

public class VerificationCodeGenerator {

	private static final String DIGITS="0123456789";
	private static final SecureRandom RANDOM=new SecureRandom();
	public static String generateVerificationCode() {
		StringBuilder code=new StringBuilder();
		for(int i=0;i<6;i++) {
			code.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
		}
		return code.toString();
	}
}