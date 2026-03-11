package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationProcessingException extends AuthenticationException {
    public AuthenticationProcessingException(String message) {
        super(message);
    }
}
