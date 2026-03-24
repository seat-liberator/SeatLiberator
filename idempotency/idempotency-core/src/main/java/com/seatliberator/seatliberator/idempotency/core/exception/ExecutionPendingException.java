package com.seatliberator.seatliberator.idempotency.core.exception;

public class ExecutionPendingException extends ExecutionException {
    public ExecutionPendingException() {
        super("Pending execution");
    }
}
