package org.hrmplatform.hrmplatform.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrmplatform.hrmplatform.dto.request.roles.UserRoleRequestDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.dto.response.UserRoleResponseDto;
import org.hrmplatform.hrmplatform.entity.User;
import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.enums.Role;
import org.hrmplatform.hrmplatform.exception.CustomErrorType;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.exception.InvalidArgumentException;
import org.hrmplatform.hrmplatform.repository.UserRoleRepository;
import org.hrmplatform.hrmplatform.view.VwUserRole;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleService {
	private final UserRoleRepository userRoleRepository;
	private final UserService userService;
	
	
	//sadece veri üzerinde değişiklik yapıldığında (insert, update, delete) gereklidir. Eğer işlem sırasında bir hata
	// veya istisna oluşursa, tüm değişiklikler geri alınır (rollback yapılır) sistem önceki güvenli duruma döner ve
	// veritabanı tutarsızlıklarından kaçınılmış olur.
	@Transactional
	public void assignRoleToUser(UserRoleRequestDto dto) {
		// Kullanıcıyı bul
		Optional<User> userOptional = userService.findById(dto.userId());
		
		if (userOptional.isEmpty()) {
			throw new HRMPlatformException(ErrorType.USER_NOTFOUND);  // Kullanıcı bulunamadığında hata fırlat
		}
		
		User user = userOptional.get();
		
		// Kullanıcının zaten belirtilen role sahip olup olmadığını kontrol et
		Optional<UserRole> existingUserRole = userRoleRepository.findByUserIdAndRole(user.getId(), dto.role());
		
		if (existingUserRole.isPresent()) {
			// Eğer kullanıcı zaten bu role sahipse, hata fırlat
			throw new InvalidArgumentException(CustomErrorType.INVALID_ROLE);
		}
		
		// Yeni rol ataması yap
		UserRole userRole = UserRole.builder().userId(user.getId()).role(dto.role()).build();
		
		// Veritabanına kaydet
		userRoleRepository.save(userRole);
	}
	
	
	public List<UserRoleResponseDto> findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		List<User> allUsers = userService.findAllUsers();
		List<UserRole> allUserRoles = userRoleRepository.findAll();
		
		// Kullanıcı rollerini eşleştirmek için bir harita oluşturuyoruz
		Map<Long, List<String>> userRolesMap = new HashMap<>();
		allUserRoles.forEach(role -> {
			userRolesMap.computeIfAbsent(role.getUserId(), k -> new ArrayList<>()).add(role.getRole().toString());
		});
		List<UserRoleResponseDto> userRoleResponseList = allUsers.stream().map(user -> {
			// Kullanıcının rollerini al, yoksa "No Role" ekle
			List<String> roles = userRolesMap.getOrDefault(user.getId(), new ArrayList<>());
			if (roles.isEmpty()) {
				roles.add("No Role");
			}
			return new UserRoleResponseDto(user.getName(), roles);
		}).collect(Collectors.toList());
		
		return userRoleResponseList;
	}
	
	public List<UserRole> findAllByUserId(Long userId) {
		return userRoleRepository.findAllByUserId(userId);
	}
	
	@Transactional
	public void deleteUserRole(UserRoleRequestDto dto) {
		userRoleRepository.deleteByUserIdAndRole(dto.userId(), Role.valueOf(String.valueOf(dto.role())));
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
	
	
	public List<UserRole> getAllUserRoleByUserId(Long userId) {
		return userRoleRepository.findByUserId(userId);
	}
	
	public Optional<UserRole> findUserRoleByUserId(Long userId) {
		return userRoleRepository.findUserRoleByUserId(userId);
	}
	
	
	// Role'a göre kullanıcı sayısını döndüren metot
	public int countByRole(Role role) {
		return userRoleRepository.countByUserRoleRole(role);
	}
	
	// Kullanıcının rolünü ID üzerinden bulan metod
	public List<UserRole> findRoleByUserId(Long userId) {
		return userRoleRepository.findByUserId(userId);
	}
	
//	// userId'ye göre UserRole'ü bulma
//	public Optional<UserRole> findByCompanyId(Long companyId) throws Exception {
//		return userRoleRepository.findByCompanyId(companyId);
//
//
//	}
	
}