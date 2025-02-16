package org.hrmplatform.hrmplatform.service;

import jakarta.validation.Valid;
import org.hrmplatform.hrmplatform.dto.request.LoginRequestDto;
import org.hrmplatform.hrmplatform.dto.request.RegisterRequestDto;
import org.hrmplatform.hrmplatform.entity.User;
import org.hrmplatform.hrmplatform.exception.*;
import org.hrmplatform.hrmplatform.repository.UserRepository;

import org.hrmplatform.hrmplatform.util.JwtManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final JwtManager jwtManager;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, JwtManager jwtManager) {
		this.userRepository = userRepository;
		this.jwtManager = jwtManager;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}
	
	
	public void register(@Valid RegisterRequestDto dto) {
		// Email zaten var mı kontrol et
		if (userRepository.findByEmail(dto.email()).isPresent()) {
			throw new HRMPlatformException(ErrorType.USER_ALREADY_EXISTS);
		}
		
		if (!dto.password().equals(dto.rePassword())) {
			throw new HRMPlatformException(ErrorType.PASSWORD_MISMATCH); //PASSWORD_MISMATCH
		}
		
		User user = User.builder()
		                .email(dto.email())
		                .password(passwordEncoder.encode(dto.password())) // Şifreyi encode ettik
		                .status(true)
		                .build();
		
		userRepository.save(user);

	}
	
	public String doLogin(@Valid LoginRequestDto dto) {
		Optional<User> userOptional = userRepository.findByEmail(dto.email());
		if (userOptional.isEmpty() || !passwordEncoder.matches(dto.password(), userOptional.get().getPassword())) {
			throw new InvalidArgumentException(CustomErrorType.INVALID_EMAIL_OR_PASSWORD);  //GIRIS BASARISIZ.
		}
		
		return jwtManager.createJWT(userOptional.get().getId());
	}
	
	public Optional<User> findById(Long userId) {
		
		return userRepository.findById(userId);
	
	}
}