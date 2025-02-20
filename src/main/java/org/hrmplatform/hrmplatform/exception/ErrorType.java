package org.hrmplatform.hrmplatform.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter

@AllArgsConstructor
public enum ErrorType implements ErrorTypeInterface {
	USERID_NOTFOUND(1001, "Kullanici id bulunamadı.", HttpStatus.BAD_REQUEST),
	VALIDATION_ERROR(400,"Validasyon hatası.. Girdiğiniz parametreler geçersizdir...",HttpStatus.BAD_REQUEST),
	INTERNAL_ERROR(500,"Sunucuda beklenmeyen hata. Lütfen daha sonra tekrar deneyin.",HttpStatus.INTERNAL_SERVER_ERROR),
	DATA_NOT_FOUND(1000, "Veri bulunamadı", HttpStatus.NOT_FOUND),
	PASSWORD_MISMATCH(3000,"Girilen şifreler uyuşmamaktadır.",HttpStatus.BAD_REQUEST),
	INVALID_ROLE(1003, "Geçersiz rol seçildi.", HttpStatus.BAD_REQUEST),
	USER_ALREADY_EXISTS(1002, "Kullanıcı zaten mevcut.", HttpStatus.BAD_REQUEST),//ErrorType
	
	// Yeni hata türleri:
	EMAIL_SENDING_FAILED(4001, "E-posta gönderimi başarısız oldu.", HttpStatus.INTERNAL_SERVER_ERROR),
	ACTIVATION_CODE_EXPIRED(4002, "Aktivasyon kodu süresi dolmuş.", HttpStatus.BAD_REQUEST),
	USER_ALREADY_ACTIVATED(4003, "Kullanıcı zaten aktif edilmiştir.", HttpStatus.BAD_REQUEST);
	
	
	
	
	private final int code;
	private final String message;
	private final HttpStatus httpStatus;
}