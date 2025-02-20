package org.hrmplatform.hrmplatform.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

//Hata aldığımızda Springin devreye girdiği yer
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	// Genel bir responseEntity oluşturan yardımcı metod
	public ResponseEntity<ErrorMessage> createResponseEntity(ErrorTypeInterface errorType, List<String> fields) {
		log.error("TÜM HATALARIN GEÇTİĞİ METOD: " + errorType.getMessage() + fields);
		return new ResponseEntity<>(ErrorMessage.builder()
		                                        .code(errorType.getCode())
		                                        .message(errorType.getMessage())
		                                        .success(false)
		                                        .fields(fields)
		                                        .build(),
		                            errorType.getHttpStatus());
	}
	
	
	// RuntimeException hatası için genel bir handler
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorMessage> runtimeExceptionHandler(RuntimeException e) {
		return createResponseEntity(ErrorType.INTERNAL_ERROR,null);
		
	}
	
	// Özel HRMPlatformException hataları için handler
	@ExceptionHandler(HRMPlatformException.class)
	public ResponseEntity<ErrorMessage> hrmPlatformExceptionHandler(HRMPlatformException e) {
		return createResponseEntity(e.getErrorType(), null);
	}
	
	// Validasyon hataları için özel handler
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> validationExceptionHandler(MethodArgumentNotValidException e) {
		log.error("Validasyon Exception "+e.getMessage());

		List<String> fieldErrors = new ArrayList<>();
		e.getBindingResult().getFieldErrors().forEach(fieldError->{
			fieldErrors.add(fieldError.getField()+" Validasyon hatası. Detay :"+fieldError.getDefaultMessage());
		});

		return createResponseEntity(ErrorType.VALIDATION_ERROR, fieldErrors);
	}
	
	// JWT doğrulama hatası için özel handler
	@ExceptionHandler(InvalidJWTException.class)
	public ResponseEntity<ErrorMessage> invalidJWTExceptionHandler(InvalidJWTException e) {
		return createResponseEntity(CustomErrorType.INVALID_JWT, null);
	}
	
	// Geçersiz parametre hatası için özel handler
	@ExceptionHandler(InvalidArgumentException.class)
	public ResponseEntity<ErrorMessage> invalidArgumentExceptionHandler(InvalidArgumentException e) {
		return createResponseEntity(e.getErrorType(), null); // Hatanın kendi türünü döndür!
	}
	
	// IllegalArgumentException hatası için özel handler
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorMessage> illegalArgumentExceptionHandler(IllegalArgumentException e) {
		log.error("IllegalArgumentException yakalandı: " + e.getMessage());
		return createResponseEntity(CustomErrorType.INVALID_ROLE, List.of(e.getMessage()));
	}
	// InvalidActivationCodeException hatası için özel handler
	@ExceptionHandler(InvalidActivationCodeException.class)
	public ResponseEntity<ErrorMessage> invalidActivationCodeExceptionHandler(InvalidActivationCodeException e) {
		return createResponseEntity(e.getErrorType(), null);
	}

}