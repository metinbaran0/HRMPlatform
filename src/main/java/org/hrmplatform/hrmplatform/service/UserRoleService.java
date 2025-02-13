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
import org.hrmplatform.hrmplatform.view.VwUserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
	public void assignRoleToUser(UserRoleRequestDto dto) {
		UserRole userRole = new UserRole();
		userRole.setUserId(dto.userId());
		userRole.setRole(Role.valueOf(dto.role()));
		userRoleRepository.save(userRole);
	}
	
	
	public List<UserRoleResponseDto> findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		Page<UserRole> userRolePage = userRoleRepository.findAll(pageable);
		Map<String, List<String>> userRolesMap = new HashMap<>();
		List<User> allUsers = userService.findAllUsers();
		allUsers.forEach(user -> {
			List<String> userRoles = userRolePage.getContent().stream()
			                                     .filter(role -> role.getUserId().equals(user.getId()))
			                                     .map(role -> role.getRole().toString())
			                                     .collect(Collectors.toList());
			if (userRoles.isEmpty()) {
				userRoles.add("No Role");
			}
			userRolesMap.put(user.getName(), userRoles);
		});
		
		List<UserRoleResponseDto> userRoleResponseList = userRolesMap.entrySet().stream()
		                                                             .map(entry -> new UserRoleResponseDto(entry.getKey(), entry.getValue()))
		                                                             .collect(Collectors.toList());
		return userRoleResponseList;
	}
	
	
	
	public List<UserRole> findAllByUserId(Long userId) {
		return userRoleRepository.findAllByUserId(userId);
	}
	@Transactional
	public void deleteUserRole(UserRoleRequestDto dto) {
		userRoleRepository.deleteByUserIdAndRole(dto.userId(), Role.valueOf(dto.role()));
	}
	
	
	public void save(UserRole doctorRole) {
		userRoleRepository.save(doctorRole);
	}
	
	public List<VwUserRole> searchByName(String name) {
		return userRoleRepository.findByName(name);
	}
	
	public List<VwUserRole> searchByRole(Role role) {
		return userRoleRepository.findByRole(role);
	}
	
	
	
}