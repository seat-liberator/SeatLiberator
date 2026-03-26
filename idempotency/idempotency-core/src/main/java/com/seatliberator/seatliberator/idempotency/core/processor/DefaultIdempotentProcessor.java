package com.seatliberator.seatliberator.idempotency.core.processor;

import com.seatliberator.seatliberator.idempotency.core.decision.ExecutionDecision;
import com.seatliberator.seatliberator.idempotency.core.decision.ExecutionDecisionEngine;
import com.seatliberator.seatliberator.idempotency.core.exception.ExecutionPendingException;
import com.seatliberator.seatliberator.idempotency.core.exception.UnknownExecutionStatusException;
import com.seatliberator.seatliberator.idempotency.core.model.ExecutionOutput;
import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyContext;
import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyKey;
import com.seatliberator.seatliberator.idempotency.core.model.ImmutableExecutionOutput;
import com.seatliberator.seatliberator.idempotency.core.store.IdempotencyRecord;
import com.seatliberator.seatliberator.idempotency.core.store.IdempotencyStore;
import org.jspecify.annotations.NonNull;
import org.springframework.resilience.annotation.Retryable;

public class DefaultIdempotentProcessor implements IdempotentProcessor {
    private final IdempotencyStore idempotencyStore;
    private final ExecutionDecisionEngine executionDecisionEngine;

    public DefaultIdempotentProcessor(
            @NonNull IdempotencyStore idempotencyStore,
            @NonNull ExecutionDecisionEngine executionDecisionEngine
    ) {
        this.idempotencyStore = idempotencyStore;
        this.executionDecisionEngine = executionDecisionEngine;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwUnchecked(Throwable throwable) throws E {
        throw (E) throwable;
    }

    @Retryable(
            includes = ExecutionPendingException.class,
            maxRetries = 10,
            delay = 50,
            multiplier = 2,
            jitter = 10,
            maxDelay = 1000
    )
    @Override
    public <T> T process(
            @NonNull IdempotencyKey key,
            @NonNull IdempotencyContext context,
            @NonNull IdempotencyAction<T> action
    ) {
        // 여러 스레드 중 단 하나만 newlyCreated == true
        IdempotencyRecord record = idempotencyStore.getOrCreate(key, context);
        ExecutionDecision decision = executionDecisionEngine.decide(record.state(), context);

        return switch (decision.kind()) {
            case EXECUTE -> processExecute(key, action);
            case REUSE_RUNNING_STATE -> processReuseRunningState();
            case REUSE_RESOLVED_RESULT -> processReuseResolvedResult(record.state().executionState().output());
            case REJECT_CONTEXT_MISMATCH -> processRejectContextMismatch(decision.throwable());
            case REJECT_EXECUTION_TIMEOUT -> processRejectExecutionTimeout(decision.throwable());
            case REJECT_ATTEMPT_LIMIT_EXCEEDED -> processRejectAttemptLimitExceeded(decision.throwable());
            default -> throw new UnknownExecutionStatusException();
        };
    }

    private <T> T processExecute(IdempotencyKey key, IdempotencyAction<T> action) {
        if (!idempotencyStore.tryMarkRunning(key)) {
            throw new ExecutionPendingException();
        }

        ExecutionOutput output;
        try {
            T result = action.execute();
            output = ImmutableExecutionOutput.success(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExecutionPendingException();
        } catch (Exception e) {
            output = ImmutableExecutionOutput.failure(e);
        }

        if (!idempotencyStore.tryMarkResolved(key, output)) {
            throw new IllegalStateException("Fail to mark resolved");
        }

        return unwrap(output);
    }

    private <T> T processReuseRunningState() {
        throw new ExecutionPendingException();
    }

    private <T> T processReuseResolvedResult(ExecutionOutput executionOutput) {
        return unwrap(executionOutput);
    }

    private <T> T processRejectContextMismatch(Throwable throwable) {
        throwUnchecked(throwable);
        throw new IllegalStateException("Unreachable");
    }

    private <T> T processRejectAttemptLimitExceeded(Throwable throwable) {
        throwUnchecked(throwable);
        throw new IllegalStateException("Unreachable");
    }

    private <T> T processRejectExecutionTimeout(Throwable throwable) {
        throwUnchecked(throwable);
        throw new IllegalStateException("Unreachable");
    }

    private RuntimeException rethrow(Throwable throwable) {
        throwUnchecked(throwable);
        throw new IllegalStateException("Unreachable");
    }

    @SuppressWarnings("unchecked")
    private <T> T unwrap(@NonNull ExecutionOutput output) {
        if (output.hasError()) {
            throwUnchecked(output.error());
            throw new IllegalStateException("Unreachable");
        }

        return (T) output.result();
    }
}
