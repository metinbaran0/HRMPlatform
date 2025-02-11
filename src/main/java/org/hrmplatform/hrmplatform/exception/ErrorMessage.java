package org.hrmplatform.hrmplatform.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 *  hata mesajı ve yanıtını HTTP formatında düzenlemek ve oluşturmak,
 *  yani code, message, fields gibi öğeleri birleştirip kullanıcıya sunmaktır.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ErrorMessage {
	Integer code;
	String message;
	Boolean success;
	
	List<String> fields;
}