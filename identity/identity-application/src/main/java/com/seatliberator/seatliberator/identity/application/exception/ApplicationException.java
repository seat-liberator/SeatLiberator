package com.seatliberator.seatliberator.identity.application.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private final ApplicationErrorCode errorCode;

    public ApplicationException(ApplicationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
