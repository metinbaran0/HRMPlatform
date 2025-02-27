package org.hrmplatform.hrmplatform.exception;

import lombok.Getter;

@Getter
public class EmployeeNotFoundException extends RuntimeException {
    private final ErrorType errorType;

    public EmployeeNotFoundException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}