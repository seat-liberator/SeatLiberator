package com.seatliberator.seatliberator.idempotency.core.exception;

public class ExecutionContextMismatchException extends ExecutionException {
    public ExecutionContextMismatchException(
            String expected,
            String actual
    ) {
        super(String.format(
                "Execution context mismatch. expected=%s, actual=%s",
                expected,
                actual
        ));
    }
}
