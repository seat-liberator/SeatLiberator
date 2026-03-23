package com.seatliberator.seatliberator.idempotency.core.decision;

import com.seatliberator.seatliberator.idempotency.core.exception.ExecutionAttemptLimitExceededException;
import com.seatliberator.seatliberator.idempotency.core.exception.ExecutionContextMismatchException;
import com.seatliberator.seatliberator.idempotency.core.exception.ExecutionTimeoutException;
import com.seatliberator.seatliberator.idempotency.core.exception.UnknownExecutionStatusException;
import com.seatliberator.seatliberator.idempotency.core.model.ExecutionState;
import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyContext;
import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyState;
import org.jspecify.annotations.NonNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class DefaultExecutionDecisionEngine implements ExecutionDecisionEngine {
    private final int maxAttemptCount;
    private final Duration timeout;
    private final Clock clock;

    public DefaultExecutionDecisionEngine(
            int maxAttemptCount,
            Duration timeout,
            Clock clock
    ) {
        if (maxAttemptCount < 1) {
            throw new IllegalArgumentException("maxAttemptCount must be >= 1");
        }
        if (timeout.isNegative()) {
            throw new IllegalArgumentException("timeout duration must be >= 1");
        }
        this.maxAttemptCount = maxAttemptCount;
        this.timeout = timeout;
        this.clock = clock;
    }

    @Override
    public @NonNull ExecutionDecision decide(
            @NonNull IdempotencyState state,
            @NonNull IdempotencyContext executionContext
    ) {
        ExecutionState executionState = state.executionState();
        IdempotencyContext originalContext = state.context();

        if (!originalContext.sameContext(executionContext)) {
            return ExecutionDecision.reject(
                    Decision.REJECT_CONTEXT_MISMATCH,
                    new ExecutionContextMismatchException(
                            originalContext.fingerprint(),
                            executionContext.fingerprint()
                    )
            );
        }

        if (executionState.isRunning()) {
            Instant now = clock.instant();
            Instant tryStartAt = Objects.requireNonNull(executionState.tryStartAt());
            Instant timeoutAt = tryStartAt.plus(timeout);

            if (now.isAfter(timeoutAt)) {
                return ExecutionDecision.reject(
                        Decision.REJECT_EXECUTION_TIMEOUT,
                        new ExecutionTimeoutException(tryStartAt, timeoutAt)
                );
            }

            return ExecutionDecision.skip(Decision.REUSE_RUNNING_STATE);
        }

        if (executionState.isResolved()) {
            return ExecutionDecision.skip(Decision.REUSE_RESOLVED_RESULT);
        }

        if (executionState.isExecutionError()) {
            int attemptCount = executionState.attemptCount();
            if (attemptCount >= maxAttemptCount) {
                return ExecutionDecision.reject(
                        Decision.REJECT_ATTEMPT_LIMIT_EXCEEDED,
                        new ExecutionAttemptLimitExceededException(maxAttemptCount, attemptCount)
                );
            }
            return ExecutionDecision.execute();
        }

        if (executionState.isPending()) {
            return ExecutionDecision.execute();
        }

        throw new UnknownExecutionStatusException();
    }
}
