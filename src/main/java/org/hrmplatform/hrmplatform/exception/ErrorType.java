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
	USER_ALREADY_EXISTS(1002, "Kullanıcı zaten mevcut.", HttpStatus.BAD_REQUEST),
	COMPANY_NOT_FOUND(400,"Şirket bulunamadı" , HttpStatus.BAD_REQUEST),
	COMPANY_ALREADY_DELETED(40,"Şirket silinmiş" , HttpStatus.BAD_REQUEST),
	EMAIL_NOT_VERIFIED(402,"Mail onaylanmamış" , HttpStatus.BAD_REQUEST),
	EMAIL_SENDING_FAILED(789,"Mail gönderirken hata oluştu" ,HttpStatus.BAD_REQUEST ),

	ACTIVATION_CODE_EXPIRED(456,"Etkinleştirme kodunun süresi doldu" , HttpStatus.BAD_REQUEST ),
	USER_ALREADY_ACTIVATED(123, "Kullanıcı zaten aktif", HttpStatus.BAD_REQUEST),
	USER_EMAIL_NOT_VERIFIED(753 ,"Kullanıcı mail onaylanmamış",HttpStatus.BAD_REQUEST );

	UNAUTHORIZED_OPERATION(403, "Bu işlemi yapmak için yetkiniz yok.", HttpStatus.FORBIDDEN);

	EMPLOYEE_NOT_FOUND(404, "Çalışan bulunamadı", HttpStatus.NOT_FOUND);





	private final int code;
	private final String message;
	private final HttpStatus httpStatus;
}