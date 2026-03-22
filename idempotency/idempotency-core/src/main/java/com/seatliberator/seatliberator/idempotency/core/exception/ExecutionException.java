package com.seatliberator.seatliberator.idempotency.core.exception;

public class ExecutionException extends RuntimeException {
    public ExecutionException(String message) {
        super(message);
    }
}
