package org.hrmplatform.hrmplatform.controller;

import jakarta.validation.Valid;
import org.hrmplatform.hrmplatform.dto.request.LoginRequestDto;
import org.hrmplatform.hrmplatform.dto.request.RegisterRequestDto;
import org.hrmplatform.hrmplatform.dto.request.ResetPasswordRequestDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.hrmplatform.hrmplatform.constant.EndPoints.*;
import org.springframework.web.bind.annotation.*;

/**
 * Kullanıcı kimlik doğrulama işlemlerini yöneten REST API Controller sınıfıdır.
 *  Güvenlik işlemleri için sorumlu sınıf.
 *  Güvenlik ve doğrulama açısından kritik olan işlemler
 * Görevleri:
 * - Kullanıcı kaydı ve aktivasyon süreci
 * - Kullanıcı giriş yapma ve JWT token döndürme
 * - Kullanıcının hesabını aktivasyon kodu ile aktifleştirme
 * - Aktivasyon e-postasını yeniden gönderme
 * - Parola sıfırlama işlemlerini yönetme
 */
@RestController
@RequestMapping(AUTH)
@CrossOrigin("*")
public class UserController {
	private final UserService userService;
	
	// Kullanıcı servisini enjekte ediyoruz
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * Kullanıcı kaydını gerçekleştirir ve aktivasyon e-postası gönderir.
	 * @param request Kullanıcı kaydını içeren DTO
	 * @return Kullanıcı kaydının başarılı olduğunu belirten cevap
	 */
	@PostMapping(REGISTER)
	public ResponseEntity<BaseResponse<Boolean>> register(@RequestBody @Valid RegisterRequestDto request) {
		userService.register(request);
		return ResponseEntity.ok(BaseResponse.<Boolean>builder()
		                                     .code(200)
		                                     .data(true)
		                                     .message("Üyelik başarı ile oluşturuldu. Lütfen aktivasyon e-postanızı kontrol edin.")
		                                     .success(true)
		                                     .build());
	}
	
	/**
	 * Kullanıcıyı giriş yapmasını sağlar ve JWT token döndürür.
	 * @param request Kullanıcı giriş bilgilerini içeren DTO
	 * @return JWT token
	 */
	@PostMapping(DOLOGIN)
	public ResponseEntity<String> doLogin(@RequestBody @Valid LoginRequestDto request) {
		String token = userService.doLogin(request);
		return ResponseEntity.ok(token);
	}
	
	/**
	 * Kullanıcıyı aktivasyon kodu ile aktifleştirir.
	 * @param code Aktivasyon kodu
	 * @return Aktivasyon işleminin başarılı olduğunu belirten cevap
	 */
	@GetMapping(ACTIVATE)
	public ResponseEntity<BaseResponse<Boolean>> activateUser(@RequestParam String code) {
		userService.activateUser(code);
		return ResponseEntity.ok(BaseResponse.<Boolean>builder()
		                                     .code(200)
		                                     .data(true)
		                                     .message("Hesap başarıyla aktifleştirildi.")
		                                     .success(true)
		                                     .build());
	}
	
	/**
	 * Kullanıcıya yeni bir aktivasyon e-postası gönderir.
	 * @param email Kullanıcının e-posta adresi
	 * @return Aktivasyon e-postasının başarıyla gönderildiğini belirten cevap
	 */
	@PostMapping(RESENDACTIVATIONEMAIL)
	public ResponseEntity<BaseResponse<Boolean>> resendActivationEmail(@RequestParam String email) {
		userService.resendActivationEmail(email);
		return ResponseEntity.ok(BaseResponse.<Boolean>builder()
		                                     .code(200)
		                                     .data(true)
		                                     .message("Aktivasyon e-postası yeniden gönderildi. Lütfen gelen kutunuzu kontrol edin.")
		                                     .success(true)
		                                     .build());
	}
	
	/**
	 * Kullanıcıya parola sıfırlama linki gönderir.
	 * @param request Kullanıcının e-posta adresini içeren DTO
	 * @return Parola sıfırlama linkinin gönderildiğini belirten cevap
	 */
	@PostMapping(FORGOTPASSWORD)
	public ResponseEntity<BaseResponse<Boolean>> forgotPassword(@RequestBody @Valid ResetPasswordRequestDto request) {
		userService.resetPassword(request);
		return ResponseEntity.ok(BaseResponse.<Boolean>builder()
		                                     .code(200)
		                                     .data(true)
		                                     .message("Parola yenileme linki e-posta adresinize gönderildi.")
		                                     .success(true)
		                                     .build());
	}
	
	/**
	 * Kullanıcıya yeni bir parola belirlemesi için gerekli işlemleri gerçekleştirir.
	 * @param request Yeni parolanın belirlenmesini sağlayan DTO
	 * @return Parola sıfırlama işleminin başarıyla gerçekleştiğini belirten cevap
	 */
	@PostMapping(RESETPASSWORD)
	public ResponseEntity<BaseResponse<Boolean>> resetPassword(@RequestBody ResetPasswordRequestDto request) {
		userService.resetPassword(request);
		return ResponseEntity.ok(BaseResponse.<Boolean>builder()
		                                     .code(200)
		                                     .data(true)
		                                     .message("Parola sıfırlama e-postası gönderildi. Lütfen gelen kutunuzu kontrol edin.")
		                                     .success(true)
		                                     .build());
	}
}