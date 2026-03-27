package com.seatliberator.seatliberator.idempotency.core.exception;

public class UnknownExecutionStatusException extends ExecutionException {
    public UnknownExecutionStatusException() {
        super("Execution state cannot be used as the basis for kind.");
    }
}
