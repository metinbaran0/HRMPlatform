package org.hrmplatform.hrmplatform.exception;

public class InvalidJWTException extends RuntimeException{
	
	private CustomErrorType customErrorType;  //code ve httpStatus almak için eşleştirmeyi yapabilmek için dahil ettik
	public InvalidJWTException(CustomErrorType customErrorType) {
		super(customErrorType.getMessage());
		this.customErrorType=customErrorType;
	}
	
	public CustomErrorType getErrorType() {
		return customErrorType;
	}
}