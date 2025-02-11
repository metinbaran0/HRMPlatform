package org.hrmplatform.hrmplatform.exception;


public class HRMPlatformException extends RuntimeException{
	private ErrorType errorType;  //code ve httpStatus almak için eşleştirmeyi yapabilmek için dahil ettik
	public HRMPlatformException(ErrorType errorType) {
		super(errorType.getMessage());
		this.errorType=errorType;
	}
	
	public ErrorType getErrorType() {
		return errorType;
	}
}