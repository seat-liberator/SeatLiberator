package com.seatliberator.seatliberator.identity.server.security.shared.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationProcessingException extends AuthenticationException {
    public AuthenticationProcessingException(String message) {
        super(message);
    }
}
