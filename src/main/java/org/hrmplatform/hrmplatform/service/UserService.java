package org.hrmplatform.hrmplatform.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hrmplatform.hrmplatform.dto.request.LoginRequestDto;
import org.hrmplatform.hrmplatform.dto.request.RegisterRequestDto;
import org.hrmplatform.hrmplatform.dto.request.ResetPasswordRequestDto;
import org.hrmplatform.hrmplatform.entity.User;
import org.hrmplatform.hrmplatform.exception.*;
import org.hrmplatform.hrmplatform.repository.UserRepository;

import org.hrmplatform.hrmplatform.util.JwtManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {	
	private final UserRepository userRepository;
	private final JwtManager jwtManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;
	
	public UserService(UserRepository userRepository, JwtManager jwtManager) {
		this.userRepository = userRepository;
		this.jwtManager = jwtManager;
	}
	
	public void register(@Valid RegisterRequestDto dto) {
		if (userRepository.findByEmail(dto.email()).isPresent()) {
			throw new HRMPlatformException(ErrorType.USER_ALREADY_EXISTS);
		}
		
		if (!dto.password().equals(dto.rePassword())) {
			throw new HRMPlatformException(ErrorType.PASSWORD_MISMATCH);
		}
		
		User user = User.builder()
		                .email(dto.email())
		                .password(passwordEncoder.encode(dto.password()))
		                .status(false)
		                .activated(false)
		                .activationCode(UUID.randomUUID().toString())
		                .activationCodeExpireAt(LocalDateTime.now().plusHours(24))
		                .build();
		
		userRepository.save(user);
		
		sendActivationEmail(user);
	}
	
	private void sendActivationEmail(User user) {
		String activationLink = "http://localhost:9090/api/auth/activate?code=" + user.getActivationCode();
		try {
			emailService.sendEmail(user.getEmail(), "Hesap Aktivasyonu",
			                       "Lütfen hesabınızı aktifleştirmek için şu linke tıklayın: " + activationLink);
		} catch (Exception e) {
			log.error("E-posta gönderimi başarısız: {}", e.getMessage());
			throw new HRMPlatformException(ErrorType.EMAIL_SENDING_FAILED);
		}
	}
	
	
	public String doLogin(@Valid LoginRequestDto dto) {
		User user = userRepository.findByEmail(dto.email())
		                          .orElseThrow(() -> new InvalidArgumentException(CustomErrorType.INVALID_EMAIL_OR_PASSWORD));
		
		if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
			throw new InvalidArgumentException(CustomErrorType.INVALID_EMAIL_OR_PASSWORD);
		}
		
		// JWT oluşturma ve döndürme
		String token = jwtManager.createToken(user.getId());
		
		// Loglama işlemi
		log.info("Generated token for user ID {}: {}", user.getId(), token);
		
		return token;
	}
	
	
	public void activateUser(String activationCode) {
		User user = userRepository.findByActivationCode(activationCode)
		                          .orElseThrow(() -> new InvalidActivationCodeException(CustomErrorType.INVALID_ACTIVATION_CODE));
		
		if (user.getActivationCodeExpireAt().isBefore(LocalDateTime.now())) {
			throw new HRMPlatformException(ErrorType.ACTIVATION_CODE_EXPIRED);
		}
		
		user.setStatus(true);
		user.setActivated(true);
		user.setActivationCode("USED");
		userRepository.save(user);
		
		jwtManager.createToken(user.getId());
	}
	
	public void resendActivationEmail(String email) {
		User user = userRepository.findByEmail(email)
		                          .orElseThrow(() -> new HRMPlatformException(ErrorType.USERID_NOTFOUND));
		
		if (user.getActivated()) {
			throw new HRMPlatformException(ErrorType.USER_ALREADY_ACTIVATED);
		}
		
		user.setActivationCode(UUID.randomUUID().toString());
		user.setActivationCodeExpireAt(LocalDateTime.now().plusHours(24));
		userRepository.save(user);
		
		sendActivationEmail(user);
	}
	public void resetPassword(ResetPasswordRequestDto dto) {
		User user = userRepository.findByEmail(dto.email())
		                          .orElseThrow(() -> new HRMPlatformException(ErrorType.USERID_NOTFOUND));
		
		String resetToken = UUID.randomUUID().toString(); // Token oluşturuluyor
		user.setResetToken(resetToken);
		user.setResetTokenExpireAt(LocalDateTime.now().plusHours(1)); // Token geçerlilik süresi
		userRepository.save(user);
		
		sendPasswordResetEmail(user);
	}
	
	
	private void sendPasswordResetEmail(User user) {
		String resetLink = "http://localhost:9090/api/auth/reset-password?token=" + user.getResetToken();
		try {
			emailService.sendEmail(user.getEmail(), "Parola Yenileme",
			                       "Parolanızı sıfırlamak için şu linki tıklayın: " + resetLink);
		} catch (Exception e) {
			log.error("E-posta gönderimi başarısız: {}", e.getMessage());
			throw new HRMPlatformException(ErrorType.EMAIL_SENDING_FAILED);
		}
	}
	
	
	
	
	public Optional<User> findById(Long userId) {
		return userRepository.findById(userId);
	}
	
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}
	
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public Optional<User> findByName(String name) {
		return userRepository.findByName(name);
	}
	
	
}