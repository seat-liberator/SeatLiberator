package com.seatliberator.seatliberator.idempotency.core.exception;

public class ExecutionAttemptLimitExceededException extends ExecutionException {
    public ExecutionAttemptLimitExceededException(
            int maxAttempts,
            int actualAttempts
    ) {
        super(String.format(
                "Execution attempts limit exceeded. limit=%d, actual=%d",
                maxAttempts,
                actualAttempts
        ));
    }
}
