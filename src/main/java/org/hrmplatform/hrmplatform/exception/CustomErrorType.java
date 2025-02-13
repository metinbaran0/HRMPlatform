package org.hrmplatform.hrmplatform.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorType implements ErrorTypeInterface {
	// JWT doğrulama hataları
	INVALID_JWT(2001, "Geçersiz veya süresi dolmuş token.", HttpStatus.UNAUTHORIZED),
	JWT_TOKEN_MISSING(2002, "JWT token gönderilmedi.", HttpStatus.BAD_REQUEST),
	
	// Geçersiz argüman hataları
	INVALID_ARGUMENT(2003, "Geçersiz argüman. Lütfen geçerli parametreler girin.", HttpStatus.BAD_REQUEST),
	ARGUMENTS_MISSING(2004, "Eksik parametre. Lütfen tüm zorunlu parametreleri doldurun.", HttpStatus.BAD_REQUEST),
	VALIDATION_TOKEN(4000,"Geçersiz token...",HttpStatus.BAD_REQUEST),
	
	// Eksik veya Beklenen Veri Hatası
	HRMPLATFORM_ID_MISSING(2005, "Hospital ID claim eksik.", HttpStatus.BAD_REQUEST),
	
	// email hatası
	EMAIL_MISSING(2007, "Email claim eksik.", HttpStatus.BAD_REQUEST),
	
	// İçsel sunucu hataları (örneğin beklenmedik hata)
	INTERNAL_SERVER_ERROR(500, "Sunucuda beklenmeyen bir hata oluştu. Lütfen tekrar deneyin.", HttpStatus.INTERNAL_SERVER_ERROR),
	
	// Kullanıcı hataları
	INVALID_ID_OR_PASSWORD(3001, "TcIdNumber veya Password yanlış", HttpStatus.BAD_REQUEST),
	INVALID_EMAIL_OR_PASSWORD(3001, "Email veya Password yanlış", HttpStatus.BAD_REQUEST),
	
	// Geçersiz rol hatası
	INVALID_ROLE(2006, "Geçersiz bir rol değeri sağlandı.", HttpStatus.BAD_REQUEST);
	
	private final int code;
	private final String message;
	private final HttpStatus httpStatus;
}