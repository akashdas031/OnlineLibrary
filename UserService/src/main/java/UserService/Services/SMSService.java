package UserService.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class SMSService {

	@Value("${twilio.accountSid}")
	private String accountSid;
	@Value("${twilio.authToken")
	private String authToken;
	@Value("${twilio.phoneNumber}")
	private String twilioPhoneNumber;
	
	public void sendVerificationCode(String phoneNumber,String verificationCode) {
		String verificationLink="localhost:6575/onlineLibrary/user/v1/verifyPhone/"+verificationCode+"?phoneNumber="+phoneNumber;
		Twilio.init(accountSid, authToken);
		Message.creator(new PhoneNumber(phoneNumber),new PhoneNumber(twilioPhoneNumber),"Click on the link to verify your Phone number With Book Inventory  :"+verificationLink).create();
		
	}
}
