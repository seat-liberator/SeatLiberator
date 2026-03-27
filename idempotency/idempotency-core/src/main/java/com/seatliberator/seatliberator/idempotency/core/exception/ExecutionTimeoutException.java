package com.seatliberator.seatliberator.idempotency.core.exception;

import java.time.Instant;

public class ExecutionTimeoutException extends ExecutionException {
    public ExecutionTimeoutException(
            Instant startAt,
            Instant timeoutAt
    ) {
        super(String.format(
                "Execution timeout. startAt=%s, timeoutAt=%s",
                startAt,
                timeoutAt
        ));
    }
}
