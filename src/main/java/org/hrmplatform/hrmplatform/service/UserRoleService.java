package org.hrmplatform.hrmplatform.service;


import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.roles.UserRoleRequestDto;
import org.hrmplatform.hrmplatform.dto.response.UserRoleResponseDto;
import org.hrmplatform.hrmplatform.entity.User;
import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.enums.Role;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.repository.UserRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleService {
	private final UserRoleRepository userRoleRepository;
	private final UserService userService;
	
	
	@Transactional
	//sadece veri üzerinde değişiklik yapıldığında (insert, update, delete) gereklidir. Eğer işlem sırasında bir hata
	// veya istisna oluşursa, tüm değişiklikler geri alınır (rollback yapılır) sistem önceki güvenli duruma döner ve
	// veritabanı tutarsızlıklarından kaçınılmış olur.
	public List<UserRole> assignRoleToUser(UserRoleRequestDto dto) {
		userService.findById(dto.userId()).orElseThrow(() -> new HRMPlatformException(ErrorType.USERID_NOTFOUND));
		List<UserRole> existingUserRoles = userRoleRepository.findByUserId(dto.userId());
		boolean roleExists = existingUserRoles.stream().anyMatch(userRole -> userRole.getRole() == dto.role());
		if (!roleExists) {
			UserRole userRole = new UserRole();
			userRole.setUserId(dto.userId());
			userRole.setRole(dto.role());
			userRoleRepository.save(userRole);
		}
		return userRoleRepository.findByUserId(dto.userId());
	}
	
	public List<UserRoleResponseDto> findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		Page<UserRole> userRolePage = userRoleRepository.findAll(pageable);
		
		List<UserRoleResponseDto> userRoleResponseList = new ArrayList<>();
		
		userRolePage.getContent().forEach(role -> {
			
			Optional<User> userOpt = userService.findById(role.getUserId());
			userOpt.ifPresent(user -> {
				UserRoleResponseDto userRoleResponseDto =
						userRoleResponseList.stream()
						                    .filter(dto -> dto.name().equals(user.getName()))
						                    .findFirst()
						                    .orElseGet(() -> {
							                    UserRoleResponseDto newUserDto = new UserRoleResponseDto(user.getName(), new ArrayList<>());
							                    userRoleResponseList.add(newUserDto);
							                    return newUserDto;
						                    });
				
			});
		});
		
		return userRoleResponseList;
	}
	
	
	
	public List<UserRole> findByUserId(Long userId) {
	return userRoleRepository.findByUserId(userId);
}

public void deleteUserRole(UserRoleRequestDto dto) {
	userRoleRepository.deleteByUserIdAndRole(dto.userId(), dto.role());
}


public void save(UserRole doctorRole) {
	userRoleRepository.save(doctorRole);
}

public List<UserRole> searchByName(String name) {
	return userRoleRepository.findByUser_NameContainingIgnoreCase(name);
}


public List<UserRole> searchByRole(Role role) {
	return userRoleRepository.findByRole(role);
}
	
}