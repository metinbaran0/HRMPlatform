package org.hrmplatform.hrmplatform.exception;

public class InvalidActivationCodeException extends RuntimeException {
	
	private CustomErrorType customErrorType;
	
	public InvalidActivationCodeException(CustomErrorType customErrorType) {
		super(customErrorType.getMessage());
		this.customErrorType = customErrorType;
	}
	
	public CustomErrorType getErrorType() {
		return customErrorType;
	}
}