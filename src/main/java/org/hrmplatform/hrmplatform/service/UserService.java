package org.hrmplatform.hrmplatform.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrmplatform.hrmplatform.dto.request.LoginRequestDto;
import org.hrmplatform.hrmplatform.dto.request.RegisterRequestDto;
import org.hrmplatform.hrmplatform.dto.request.ResetPasswordRequestDto;
import org.hrmplatform.hrmplatform.dto.request.UpdateUserRequestDto;
import org.hrmplatform.hrmplatform.dto.request.roles.UserRoleRequestDto;
import org.hrmplatform.hrmplatform.dto.response.DoLoginResponseDto;
import org.hrmplatform.hrmplatform.dto.response.UserProfileResponseDto;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.entity.User;
import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.enums.Role;
import org.hrmplatform.hrmplatform.exception.*;
import org.hrmplatform.hrmplatform.repository.UserRepository;

import org.hrmplatform.hrmplatform.util.JwtManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
private final UserRepository userRepository;
	private final JwtManager jwtManager;
	
	@Lazy
	private UserRoleService userRoleService;
	private PasswordService passwordService;

	
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;
	
	public UserService(UserRepository userRepository, JwtManager jwtManager, @Lazy UserRoleService userRoleService) {
		this.userRepository = userRepository;
		this.jwtManager = jwtManager;
		this.userRoleService = userRoleService;
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
	
	
	public DoLoginResponseDto doLogin(@Valid LoginRequestDto dto) {
		User user = userRepository.findByEmail(dto.email())
		                          .orElseThrow(() -> new InvalidArgumentException(CustomErrorType.INVALID_EMAIL_OR_PASSWORD));
		
		if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
			throw new InvalidArgumentException(CustomErrorType.INVALID_EMAIL_OR_PASSWORD);
		}
		UserRole userRole = userRoleService.findUserRoleByUserId(user.getId())
				.orElseThrow(() -> new HRMPlatformException(ErrorType.USER_ROLE_NOT_FOUND));

		Role role = userRole.getRole();  // UserRole içindeki Role enum'unu al

		// JWT oluşturma
		String token = jwtManager.createToken(
				user.getId(),          // authId
				user.getEmail(),       // email
				role,                 // role
				user.getCompanyId(),   // companyId
				user.getActivated(),  // activated
				user.getStatus() ,
				user.getEmployeeId());
		

		// Kullanıcının rolünü çek

		// Loglama işlemi
		log.info("Generated token for user ID {}: {}, Role: {}", user.getId(), token, role);

		return new DoLoginResponseDto(role, user.getId(), token );
	}

	
	public void activateUser(String activationCode) {
		User user = userRepository.findByActivationCode(activationCode)
		                          .orElseThrow(() -> new InvalidActivationCodeException(CustomErrorType.INVALID_ACTIVATION_CODE));
		
		if (user.getActivationCodeExpireAt().isBefore(LocalDateTime.now())) {
			throw new HRMPlatformException(ErrorType.ACTIVATION_CODE_EXPIRED);
		}

		// JWT oluştur
		UserRole userRole = userRoleService.findUserRoleByUserId(user.getId())
				.orElseThrow(() -> new HRMPlatformException(ErrorType.USER_ROLE_NOT_FOUND));

		Role role = userRole.getRole();

		String token = jwtManager.createToken(
				user.getId(),          // authId
				user.getEmail(),       // email
				role,                 // role
				user.getCompanyId(),   // companyId
				user.getActivated(),  // activated
				user.getStatus() ,     // status
				user.getEmployeeId());

		// Loglama işlemi
		log.info("User activated: {} - New token generated: {}", user.getEmail(), token);
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
	
	public UserProfileResponseDto getUserById(Long userId) {
		User user = userRepository.findById(userId)
		                          .orElseThrow(() -> new HRMPlatformException(ErrorType.USERID_NOTFOUND));
		
		return new UserProfileResponseDto(
				user.getName(),
				user.getEmail(),
				user.getActivated(),
				user.getCreatedAt(),
				user.getUpdatedAt()
		);
	}
	
	public boolean updateUser(UpdateUserRequestDto request) {
		User user = userRepository.findByEmail(request.email())
		                          .orElseThrow(() -> new HRMPlatformException(ErrorType.USER_NOTFOUND));
		
		user.setName(request.name());
		user.setUpdatedAt(LocalDateTime.now());
		
		userRepository.save(user);
		return true;
	}


	public void registerCompanyAdmin(Company company) {
		// Eğer e-posta zaten kayıtlıysa hata fırlat
		if (userRepository.findByEmail(company.getEmail()).isPresent()) {
			throw new HRMPlatformException(ErrorType.USER_ALREADY_EXISTS);
		}
		// Rastgele bir şifre oluştur
		String generatedPassword = passwordService.generateRandomPassword();

		// Şifreyi hash'le
		String hashedPassword = passwordEncoder.encode(generatedPassword);


		// Kullanıcıyı (User) oluştur ve kaydet
		User user = User.builder()
				.name(company.getContactPerson() )
				.email(company.getEmail())
				.password(hashedPassword)  // Hashlenmiş şifreyi ekliyoruz
				.status(true)  // Kullanıcı aktif
				.companyId(company.getId())
				.employeeId(null)
				.activated(false)  // İlk başta aktif değilse
				.activationCode(null)  // Eğer aktivasyon gerekmiyorsa null
				.activationCodeExpireAt(null)
				.resetToken(null)
				.resetTokenExpireAt(null)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		userRepository.save(user);

		// Kullanıcıya CompanyAdmin rolü ata
		userRoleService.assignRoleToUser(new UserRoleRequestDto(user.getId(), Role.COMPANY_ADMIN));

		// Aktivasyon mailini gönder
		sendActivationEmail(user);
	}

	
	
	
	//           METIN
	
	public int getTotalAdminCount() {
		return userRoleService.countByRole(Role.valueOf("COMPANY_ADMIN"));
	}
	
	public int getTotalEmployeeCount() {
		return userRoleService.countByRole(Role.valueOf("EMPLOYEE"));
	}
	
	public void save(User user) {
		userRepository.save(user);
	}
	
//	// Kullanıcıyı pasif hale getirme işlemi
//	@Transactional
//	public void deactivateUser(Long companyId) throws Exception {
//
//		UserRole userRole = userRoleService.findByCompanyId(companyId)
//		                                   .orElseThrow(() -> new Exception("Kullanıcı bulunamadı"));
//
//
//		if (userRole.getRole().equals(Role.COMPANY_ADMIN)) {
//			// 3. Kullanıcının ait olduğu şirketteki tüm çalışanları pasif hale getir
//			Long company = employeeService.findByUserId(companyId)
//			                                .orElseThrow(() -> new Exception("Çalışan bilgisi bulunamadı"))
//			                                .getCompanyId();
//
//			employeeService.deactivateEmployeesByCompanyId(company);
//		}
//
//		User user = findById(userRole.getUserId())
//		                       .orElseThrow(() -> new Exception("Kullanıcı bilgisi bulunamadı"));
//		user.setActivated(false);
//		userRepository.save(user);
//		userRoleService.save(userRole);
//	}
	
}