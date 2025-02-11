package org.hrmplatform.hrmplatform.exception;

public class DoLoginException extends RuntimeException {
	private final ErrorType errorType;
	
	public DoLoginException(ErrorType errorType) {
		super(errorType.getMessage()); // Artık getMessage() var!
		this.errorType = errorType;
	}
	
	public ErrorType getErrorType() {
		return errorType;
	}
}