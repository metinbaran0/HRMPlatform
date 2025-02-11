package org.hrmplatform.hrmplatform.exception;

import org.springframework.http.HttpStatus;

/**
 * hata türü hakkında bilgi içerir.
 * Bu arayüz, sadece hata türünün code, message ve HttpStatus bilgilerini sağlayacak şekilde yapılandırılmıştır.
 */
public interface ErrorTypeInterface {
	int getCode();
	String getMessage();
	HttpStatus getHttpStatus();
}