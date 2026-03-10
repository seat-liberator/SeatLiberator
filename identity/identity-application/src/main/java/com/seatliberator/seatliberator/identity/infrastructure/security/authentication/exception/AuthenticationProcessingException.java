package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.exception;

public class AuthenticationProcessingException extends AuthenticationException {
    public AuthenticationProcessingException(String message) {
        super("PROCESSING_FAILED", message);
    }
}
