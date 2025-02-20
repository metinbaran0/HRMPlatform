package org.hrmplatform.hrmplatform.service;

import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		try {
			mailSender.send(message);
		} catch (Exception e) {
			// Burada daha fazla log ekleyebilirsiniz
			System.out.println("E-posta gönderim hatası: " + e.getMessage());  // Konsola yazdırabilirsiniz
			throw new HRMPlatformException(ErrorType.EMAIL_SENDING_FAILED  );
		}
	}
	
}