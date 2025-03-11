package org.hrmplatform.hrmplatform.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordService {
	private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DIGITS = "0123456789";
	private static final String SPECIAL_CHARS = "@#$%^&+=*";
	
	private static final String ALL_CHARACTERS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARS;
	
	private final SecureRandom random = new SecureRandom();
	private final EmailService emailService;
	
	public PasswordService(EmailService emailService) {
		this.emailService = emailService;
	}
	
	public String generateRandomPassword() {
		StringBuilder password = new StringBuilder();
		
		password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
		password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
		password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
		password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
		
		for (int i = password.length(); i < 12; i++) {
			password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
		}
		
		return password.toString();
	}
	
	public void sendRandomPasswordToUser(String userEmail) {
		String password = generateRandomPassword();
		
		// Şifreyi e-posta ile gönder
		emailService.sendEmail(userEmail, "Yeni Şifreniz", "Yeni şifreniz: " + password);
		
		// Şifreyi hash'leyip veritabanına kaydedin
		saveHashedPassword(userEmail, password);
	}
	
	public void saveHashedPassword(String email, String plainPassword) {
		String hashedPassword = new BCryptPasswordEncoder().encode(plainPassword);
		
		// Burada hashed şifreyi kullanıcı kaydına ekleyebilirsiniz.
		// Örneğin: userRepository.updatePassword(email, hashedPassword);
	}
}