package org.hrmplatform.hrmplatform.exception;

public class InvalidArgumentException extends IllegalArgumentException{
	private CustomErrorType customErrorType;  //code ve httpStatus almak için eşleştirmeyi yapabilmek için dahil ettik
	public InvalidArgumentException(CustomErrorType customErrorType) {
		super(customErrorType.getMessage());
		this.customErrorType=customErrorType;
	}
	
	public CustomErrorType getErrorType() {
		return customErrorType;
	}
}