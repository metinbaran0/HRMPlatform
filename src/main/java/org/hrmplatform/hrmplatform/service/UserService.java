package org.hrmplatform.hrmplatform.service;

import jakarta.validation.Valid;
import org.hrmplatform.hrmplatform.dto.request.LoginRequestDto;
import org.hrmplatform.hrmplatform.dto.request.RegisterRequestDto;
import org.hrmplatform.hrmplatform.entity.User;
import org.hrmplatform.hrmplatform.exception.DoLoginException;
import org.hrmplatform.hrmplatform.exception.ErrorType;
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
	
	
	
	
	public String register(@Valid RegisterRequestDto dto) {
		if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
			throw new DoLoginException(ErrorType.USER_NOT_FOUND);
		}
		
		User user = User.builder()
		                .name(dto.getName())
		                .email(dto.getEmail())
		                .password(passwordEncoder.encode(dto.getPassword())) // Şifreyi encode ettik
		                .role(dto.getRole())
		                .status(true)
		                .build();
		
		userRepository.save(user);
		return "Kullanıcı başarıyla kaydedildi.";
	}
	
	public String doLogin(@Valid LoginRequestDto dto) {
		Optional<User> userOptional = userRepository.findByEmail(dto.getEmail());
		if (userOptional.isEmpty() || !passwordEncoder.matches(dto.getPassword(), userOptional.get().getPassword())) {
			throw new DoLoginException(ErrorType.INVALID_PASSWORD);
		}
		
		return jwtManager.createToken(userOptional.get().getId());
	}
}