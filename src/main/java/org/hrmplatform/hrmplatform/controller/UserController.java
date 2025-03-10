package org.hrmplatform.hrmplatform.controller;

import jakarta.validation.Valid;
import org.hrmplatform.hrmplatform.config.JwtUserDetails;
import org.hrmplatform.hrmplatform.dto.request.LoginRequestDto;
import org.hrmplatform.hrmplatform.dto.request.RegisterRequestDto;
import org.hrmplatform.hrmplatform.dto.request.ResetPasswordRequestDto;
import org.hrmplatform.hrmplatform.dto.request.UpdateUserRequestDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.dto.response.DoLoginResponseDto;
import org.hrmplatform.hrmplatform.dto.response.UserProfileResponseDto;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.exception.InvalidArgumentException;
import org.hrmplatform.hrmplatform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.apache.naming.ResourceRef.*;
import static org.hrmplatform.hrmplatform.constant.EndPoints.*;
import static org.hrmplatform.hrmplatform.constant.EndPoints.AUTH;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Kullanıcı kimlik doğrulama işlemlerini yöneten REST API Controller sınıfıdır.
 * Güvenlik işlemleri için sorumlu sınıf.
 * Güvenlik ve doğrulama açısından kritik olan işlemler
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
     *
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
     *
     * @param request Kullanıcı giriş bilgilerini içeren DTO
     * @return JWT token ve kullanıcı bilgileri
     */
    @PostMapping(DOLOGIN)
    public ResponseEntity<BaseResponse<DoLoginResponseDto>> doLogin(@RequestBody @Valid LoginRequestDto request) {
        try {
            // Service katmanından giriş işlemini gerçekleştir
            DoLoginResponseDto response = userService.doLogin(request);

            // Başarılı yanıt oluştur
            return ResponseEntity.ok(BaseResponse.<DoLoginResponseDto>builder()
                    .code(200)
                    .data(response)
                    .message("Giriş başarılı")
                    .success(true)
                    .build());

        } catch (InvalidArgumentException ex) {
            // Geçersiz kimlik doğrulama bilgileri durumunda 401 Unauthorized dön
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.<DoLoginResponseDto>builder()
                            .code(401)
                            .message(ex.getMessage())
                            .success(false)
                            .data(null)
                            .build());

        } catch (HRMPlatformException ex) {
            // Diğer hatalar için 500 Internal Server Error dön
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<DoLoginResponseDto>builder()
                            .code(500)
                            .message("Sunucu hatası: " + ex.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        }
    }
    
    /**
     * Kullanıcıyı aktivasyon kodu ile aktifleştirir.
     *
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
     *
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
     *
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
     *
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
    
    
//    //Kendi profil bilgilerini alabilmeli ve Profil bilgilerini güncelleyebilmeli (ad, e-posta, şifre vs.)
//
//    // Yeni eklenen endpointler
@GetMapping("/getprofile")
public ResponseEntity<BaseResponse<UserProfileResponseDto>> getUserProfile(@RequestParam Long userId) {
    UserProfileResponseDto userResponse = userService.getUserById(userId);
    
    return ResponseEntity.ok(BaseResponse.<UserProfileResponseDto>builder()
                                         .code(200)
                                         .data(userResponse)
                                         .message("Kullanıcı bilgileri getirildi.")
                                         .success(true)
                                         .build());
}
    
    @PutMapping("/updateprofile")
    public ResponseEntity<BaseResponse<Boolean>> updateUserProfile(
            @RequestBody @Valid UpdateUserRequestDto request) {
        
        userService.updateUser(request);
        
        return ResponseEntity.ok(BaseResponse.<Boolean>builder()
                                             .code(200)
                                             .data(true)
                                             .message("Kullanıcı bilgileri güncellendi.")
                                             .success(true)
                                             .build());
    }
    
//
//    //Kullanıcı hesabını pasif hale getirir
//    @PatchMapping("/users/deactivate")
//    public ResponseEntity<BaseResponse<String>> deactivateUser(@RequestParam Long companyId) {
//        try {
//            userService.deactivateUser(companyId);
//            return ResponseEntity.ok(BaseResponse.<String>builder()
//                                                 .code(200)
//                                                 .message("Kullanıcı başarıyla pasif hale getirildi")
//                                                 .success(true)
//                                                 .data("Kullanıcı hesabı pasif hale getirildi")
//                                                 .build());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                                 .body(BaseResponse.<String>builder()
//                                                   .code(500)
//                                                   .message("Hata oluştu: " + e.getMessage())
//                                                   .success(false)
//                                                   .data(null)
//                                                   .build());
//        }
//    }
  
}