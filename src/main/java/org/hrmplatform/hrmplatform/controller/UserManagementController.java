package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.UpdateUserRequestDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.dto.response.UserProfileResponseDto;
import org.hrmplatform.hrmplatform.service.UserManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

/**
 * Kullanıcı yönetimi işlemlerini gerçekleştiren REST API Controller sınıfıdır.
 * Kullanıcı bilgilerini yönetme ve kullanıcı profili gibi veritabanı işlemleri için sorumlu sınıf.
 *
 * Görevleri:
 * - Kullanıcı profilini getirme
 * - Kullanıcı bilgilerini güncelleme
 * - Sistemdeki tüm kullanıcıları listeleme
 */
@RestController
@RequestMapping(USER)
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserManagementController {
	private final UserManagementService userManagementService;
	
	
	
	/**
	 * Kullanıcı profilini getirir.
	 * @param userId Kullanıcının ID'si
	 * @return Kullanıcının profil bilgileri
	 */
	//@GetMapping(PROFILE)
	public ResponseEntity<BaseResponse<UserProfileResponseDto>> getUserProfile(@PathVariable Long userId) {
		UserProfileResponseDto profile = userManagementService.getUserProfile(userId);
		return ResponseEntity.ok(BaseResponse.<UserProfileResponseDto>builder()
		                                     .code(200)
		                                     .data(profile)
		                                     .message("Kullanıcı profili başarıyla getirildi.")
		                                     .success(true)
		                                     .build());
	}
	
	/**
	 * Kullanıcı bilgilerini günceller.
	 * @param request Güncellenmiş kullanıcı bilgilerini içeren DTO
	 * @return Güncelleme işleminin başarılı olduğunu belirten cevap
	 */
//	@PutMapping(UPDATE)
	public ResponseEntity<BaseResponse<Boolean>> updateUser(@RequestBody UpdateUserRequestDto request) {
		userManagementService.updateUser(request);
		return ResponseEntity.ok(BaseResponse.<Boolean>builder()
		                                     .code(200)
		                                     .data(true)
		                                     .message("Kullanıcı bilgileri başarıyla güncellendi.")
		                                     .success(true)
		                                     .build());
	}
	
	/**
	 * Tüm kullanıcıları getirir.
	 * @return Kullanıcı listesi
	 */
	
	//@GetMapping(FINDALL_USERS)
	public ResponseEntity<BaseResponse<List<UserProfileResponseDto>>> getAllUsers() {
		List<UserProfileResponseDto> users = userManagementService.getAllUsers();
		return ResponseEntity.ok(BaseResponse.<List<UserProfileResponseDto>>builder()
		                                     .code(200)
		                                     .data(users)
		                                     .message("Tüm kullanıcılar başarıyla getirildi.")
		                                     .success(true)
		                                     .build());
	}
	
	/**
	 * Kullanıcıyı sistemden siler.
	 * @param userId Silinecek kullanıcının ID'si
	 * @return Kullanıcı silme işleminin başarılı olduğunu belirten cevap
	 */
//	@DeleteMapping(DELETE)
	public ResponseEntity<BaseResponse<Boolean>> deleteUser(@PathVariable Long userId) {
		userManagementService.deleteUser(userId);
		return ResponseEntity.ok(BaseResponse.<Boolean>builder()
		                                     .code(200)
		                                     .data(true)
		                                     .message("Kullanıcı başarıyla silindi.")
		                                     .success(true)
		                                     .build());
	}
	
	
	/**
	 * Kullanıcının durumunu aktif veya pasif hale getirir.
	 * @param userId Kullanıcının ID'si
	 * @param isActive Kullanıcının aktif olup olmadığını belirten değer
	 * @return Kullanıcı durumu güncelleme işleminin başarılı olduğunu belirten cevap
	 */
	//@PutMapping(STATUS)
	public ResponseEntity<BaseResponse<Boolean>> updateUserStatus(@PathVariable Long userId, @RequestParam Boolean isActive) {
		userManagementService.updateUserStatus(userId, isActive);
		return ResponseEntity.ok(BaseResponse.<Boolean>builder()
		                                     .code(200)
		                                     .data(true)
		                                     .message("Kullanıcı durumu başarıyla güncellendi.")
		                                     .success(true)
		                                     .build());
	}
	
	
	/**
	 * Kullanıcıları belirli kriterlere göre arar.
	 * @param name Kullanıcı adı
	 * @param email Kullanıcı e-posta adresi
	 * @return Arama kriterlerine göre bulunan kullanıcılar
	 */
	//@GetMapping(SEARCH)
	
	
	
	
	public ResponseEntity<BaseResponse<List<UserProfileResponseDto>>> searchUsers(@RequestParam(required = false) String name, @RequestParam(required = false) String email) {
		List<UserProfileResponseDto> users = userManagementService.searchUsers(name, email);
		return ResponseEntity.ok(BaseResponse.<List<UserProfileResponseDto>>builder()
		                                     .code(200)
		                                     .data(users)
		                                     .message("Kullanıcılar başarıyla arandı.")
		                                     .success(true)
		                                     .build());
	}
	
	
}