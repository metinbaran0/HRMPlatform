package org.hrmplatform.hrmplatform.exception;

import lombok.Getter;

@Getter

@AllArgsConstructor
public enum ErrorType implements ErrorTypeInterface {
	USERID_NOTFOUND(1001, "Kullanici id bulunamadı.", HttpStatus.BAD_REQUEST),
	VALIDATION_ERROR(400,"Validasyon hatası.. Girdiğiniz parametreler geçersizdir...",HttpStatus.BAD_REQUEST),
	INTERNAL_ERROR(500,"Sunucuda beklenmeyen hata. Lütfen daha sonra tekrar deneyin.",HttpStatus.INTERNAL_SERVER_ERROR),
	DATA_NOT_FOUND(1000, "Veri bulunamadı", HttpStatus.NOT_FOUND),
	PASSWORD_MISMATCH(3000,"Girilen şifreler uyuşmamaktadır.",HttpStatus.BAD_REQUEST),
	INVALID_ROLE(1003, "Geçersiz rol seçildi.", HttpStatus.BAD_REQUEST),
	USER_ALREADY_EXISTS(1002, "Kullanıcı zaten mevcut.", HttpStatus.BAD_REQUEST);//ErrorType


	
	
	private final String message;
}