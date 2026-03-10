package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {
    private final String errorCode;

    public AuthenticationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
