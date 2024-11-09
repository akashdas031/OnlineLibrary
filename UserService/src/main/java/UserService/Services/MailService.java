package UserService.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

	@Autowired
	private  JavaMailSender mailSender;
	
	public void sendVerificationMail(String to,String token) {
		String verificationLink="localhost:6575/onlineLibrary/user/v1/verify/"+token;
		SimpleMailMessage message=new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Verification Mail From Book Inventory");
		message.setText("Please Click on the Link to Verify your Mail..."+verificationLink);
		mailSender.send(message);
	}
	
}
