package org.hrmplatform.hrmplatform.service;

import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.dto.request.EmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	// E-posta gönderme metodu (String parametreler)
	public void sendEmail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		
		try {
			mailSender.send(message);
			logger.info("E-posta başarıyla gönderildi: {}", to);
		} catch (Exception e) {
			logger.error("E-posta gönderim hatası: {}", e.getMessage(), e);
			throw new HRMPlatformException(ErrorType.EMAIL_SENDING_FAILED);
		}
	}
	
	// E-posta gönderme metodu (EmailRequest nesnesi)
	public void sendEmail(EmailRequest emailRequest) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(emailRequest.to());
		message.setSubject(emailRequest.subject());
		message.setText(emailRequest.text());
		
		try {
			mailSender.send(message);
			logger.info("E-posta başarıyla gönderildi: {}", emailRequest.to());
		} catch (Exception e) {
			logger.error("E-posta gönderimi sırasında hata oluştu: {}", e.getMessage(), e);
			throw new HRMPlatformException(ErrorType.EMAIL_SENDING_FAILED);
		}
	}
}