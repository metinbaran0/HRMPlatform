package org.hrmplatform.hrmplatform.service;
import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.UpdateUserRequestDto;
import org.hrmplatform.hrmplatform.dto.response.UserProfileResponseDto;
import org.hrmplatform.hrmplatform.entity.User;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {
	private final UserRepository userRepository;
	private final UserService userService;
	
	
	
	/**
	 * Kullanıcı profilini getirir.
	 * @param userId Kullanıcının ID'si
	 * @return Kullanıcının profil bilgileri
	 */
	public UserProfileResponseDto getUserProfile(Long userId) {
		User user = userRepository.findById(userId)
		                          .orElseThrow(() -> new HRMPlatformException(ErrorType.USERID_NOTFOUND));
		return convertToUserProfileResponseDto(user);
	}
	
	/**
	 * Kullanıcı bilgilerini günceller.
	 * @param request Güncellenmiş kullanıcı bilgilerini içeren DTO
	 */
	@Transactional
	public void updateUser(UpdateUserRequestDto request) {
		User user = userService.findById(request.userId())
		                       .orElseThrow(() -> new HRMPlatformException(ErrorType.USERID_NOTFOUND));
		
		
		
		if (request.name() != null) {
			user.setName(request.name());
		}
		
		
		userRepository.save(user);
	}
	
	/**
	 * Tüm kullanıcıları getirir.
	 * @return Kullanıcı listesi
	 */
	public List<UserProfileResponseDto> getAllUsers() {
		List<User> users = userRepository.findAll();
		return users.stream()
		            .map(this::convertToUserProfileResponseDto)
		            .collect(Collectors.toList());
	}
	
	/**
	 * User modelini UserProfileResponseDto'ya dönüştürür.
	 * @param user Kullanıcı nesnesi
	 * @return UserProfileResponseDto
	 */
	private UserProfileResponseDto convertToUserProfileResponseDto(User user) {
		return new UserProfileResponseDto(
				user.getName(),
				user.getEmail(),
				user.getStatus(),
				user.getCreatedAt(),
				user.getUpdatedAt()
		
		);
	}
	
	/**
	 * Kullanıcıyı sistemden siler.
	 * @param userId Silinecek kullanıcının ID'si
	 */
	@Transactional
	public void deleteUser(Long userId) {
		User user = userRepository.findById(userId)
		                          .orElseThrow(() -> new HRMPlatformException(ErrorType.USERID_NOTFOUND));
		userRepository.delete(user);
	}
	
	
	/**
	 * Kullanıcının durumunu aktif veya pasif hale getirir.
	 * @param userId Kullanıcının ID'si
	 * @param isActive Kullanıcının aktif olup olmadığını belirten değer
	 */
	@Transactional
	public void updateUserStatus(Long userId, Boolean isActive) {
		User user = userRepository.findById(userId)
		                          .orElseThrow(() -> new HRMPlatformException(ErrorType.USERID_NOTFOUND));
		user.setStatus(Boolean.valueOf(isActive ? "ACTIVE" : "INACTIVE"));
		userRepository.save(user);
	}
	
	/**
	 * Kullanıcıları belirli kriterlere göre arar.
	 * @param name Kullanıcı adı
	 * @param email Kullanıcı e-posta adresi
	 * @return Arama sonuçları
	 */
	public List<UserProfileResponseDto> searchUsers(String name, String email) {
		List<User> users = userRepository.findByNameContainingAndEmailContaining(name, email);
		return users.stream()
		            .map(this::convertToUserProfileResponseDto)
		            .collect(Collectors.toList());
	}
	
}