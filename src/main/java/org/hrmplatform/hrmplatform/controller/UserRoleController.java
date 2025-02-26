package org.hrmplatform.hrmplatform.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrmplatform.hrmplatform.dto.request.roles.UserRoleRequestDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.dto.response.UserRoleResponseDto;
import org.hrmplatform.hrmplatform.entity.User;
import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.enums.Role;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.service.UserRoleService;
import org.hrmplatform.hrmplatform.service.UserService;
import org.hrmplatform.hrmplatform.util.JwtManager;
import org.hrmplatform.hrmplatform.view.VwUserRole;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;


/**
 * Kullanıcı rolleriyle ilgili işlemleri yöneten REST API Controller sınıfıdır.
 *
 * Görevleri:
 * - Kullanıcılara belirli roller atamak
 * - Kullanıcının sahip olduğu rolleri sorgulamak
 * - Tüm kullanıcı rollerini listelemek
 * - Belirli bir kullanıcı rolünü silmek
 */
@RestController
@RequestMapping(USERROLE)
@RequiredArgsConstructor
@Slf4j
public class UserRoleController {
	private final UserRoleService userRoleService;
	private final UserService userService;
	
	
	/**
	 *  Kullanıcıya belirli bir rol atar.
	 * @param dto Kullanıcının ID'si ve atanacak rolü içeren DTO nesnesi
	 * @return Kullanıcıya atanmış rollerin listesi
	 */
	@Transactional
	@PostMapping(ASSIGNROLES)
	@PreAuthorize("hasAnyAuthority('SITE_ADMIN', 'COMPANY_ADMIN')")
	public ResponseEntity<BaseResponse<Boolean>> assignRoleToUser(@RequestBody UserRoleRequestDto dto) {
		userRoleService.assignRoleToUser(dto);
		
		return ResponseEntity.ok(BaseResponse.<Boolean>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(true)
		                                     .message("User Role Assigned")
		                                     .build());
	}
	
	
	
	/**
	 * Sistemdeki tüm kullanıcıları ve onların rollerini listeler.
	 * @return Kullanıcı ve rollerin listesini döner
	 */
	@GetMapping(FINDALL)
	public ResponseEntity<BaseResponse<List<UserRoleResponseDto>>> findAll(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size) {
		
		List<UserRoleResponseDto> userRoleResponseList = userRoleService.findAll(page, size);
		
		return ResponseEntity.ok(
				BaseResponse.<List<UserRoleResponseDto>>builder()
				            .message("Tüm kullanıcılar ve roller listelendi.")
				            .code(200)
						    .success(true)
				            .data(userRoleResponseList)
				            .build()
		);
	}

	/**
	 * Belirli bir kullanıcıya ait tüm rolleri döndürür.
	 * @param userId Kullanıcının ID’si
	 * @return Kullanıcının sahip olduğu rollerin listesi
	 */
	@GetMapping(FINDBYUSERID)
	public ResponseEntity<BaseResponse<List<UserRole>>> findByUserId(@RequestParam Long userId) {
		return ResponseEntity.ok(BaseResponse.<List<UserRole>>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(userRoleService.findAllByUserId(userId))
		                                     .message("User yetkiler listelendi")
		                                     .build());
	}
	
	/**
	 * Belirli bir kullanıcının rolünü sistemden siler.
	 *
	 * @param dto Kullanıcının ID’si ve silinecek rol bilgisini içeren DTO.
	 * @return HTTP 204 No Content: Kaynak başarıyla silindi, ancak herhangi bir veri geri dönmemektedir.
	 */
	@Transactional
	@DeleteMapping(DELETEUSERROLE)
	public void deleteUserRole(@RequestBody  UserRoleRequestDto dto) {
		userRoleService.deleteUserRole(dto);
		ResponseEntity.noContent().build();
	}
	
	
	/**
	 * Kullanıcı adı ile arama yaparak kullanıcı ve rollerini listeler.
	 * @param name Kullanıcı adı
	 * @return Arama kriterlerine göre bulunan kullanıcı ve rollerin listesi
	 */
	@GetMapping(SEARCHBYNAME)
	public List<VwUserRole> searchByName(@RequestParam String name) {
		return userRoleService.searchByName(name);
	}
	
	/**
	 * Rol ile arama yaparak o role sahip tüm kullanıcıları listeler.
	 * @param role Rol adı
	 * @return O role sahip tüm kullanıcıların listesi
	 */
	@GetMapping(SEARCHBYROLE)
	public List<VwUserRole> searchByRole(@RequestParam Role role) {
		return userRoleService.searchByRole(role);
	}
	
	
	
}