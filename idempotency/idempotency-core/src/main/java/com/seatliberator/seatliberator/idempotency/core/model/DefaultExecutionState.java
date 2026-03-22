package com.seatliberator.seatliberator.idempotency.core.model;

import com.seatliberator.seatliberator.idempotency.core.util.ExecutionStateValidator;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public class DefaultExecutionState implements ExecutionState {
    private @NonNull ExecutionPhase phase;
    private int attemptCount;
    private @Nullable Instant tryStartAt;
    private @Nullable Instant resolvedAt;
    private @Nullable ExecutionOutput output;

    public DefaultExecutionState(
            @NonNull ExecutionPhase phase,
            int attemptCount,
            @Nullable Instant tryStartAt,
            @Nullable Instant resolvedAt,
            @Nullable ExecutionOutput output
    ) {
        if (attemptCount < 0) {
            throw new IllegalArgumentException("attemptCount must not be negative.");
        }

        this.phase = phase;
        this.attemptCount = attemptCount;
        this.tryStartAt = tryStartAt;
        this.resolvedAt = resolvedAt;
        this.output = output;

        ExecutionStateValidator.validate(this);
    }

    public static DefaultExecutionState of(ExecutionState executionState) {
        return new DefaultExecutionState(
                executionState.phase(),
                executionState.attemptCount(),
                executionState.tryStartAt(),
                executionState.resolvedAt(),
                executionState.output()
        );
    }

    public static DefaultExecutionState pending() {
        return new DefaultExecutionState(
                ExecutionPhase.PENDING,
                0,
                null,
                null,
                null
        );
    }

    public static DefaultExecutionState running(
            int attemptCount,
            @NonNull Instant tryStartAt
    ) {
        return new DefaultExecutionState(
                ExecutionPhase.RUNNING,
                attemptCount,
                tryStartAt,
                null,
                null
        );
    }

    public static DefaultExecutionState resolved(
            int attemptCount,
            @NonNull Instant tryStartAt,
            @NonNull Instant resolvedAt,
            @NonNull ExecutionOutput output
    ) {
        return new DefaultExecutionState(
                ExecutionPhase.RESOLVED,
                attemptCount,
                tryStartAt,
                resolvedAt,
                output
        );
    }

    public static DefaultExecutionState executionError(
            int attemptCount,
            @NonNull Instant tryStartAt
    ) {
        return new DefaultExecutionState(
                ExecutionPhase.EXECUTION_ERROR,
                attemptCount,
                tryStartAt,
                null,
                null
        );
    }

    public void markRunning(
            @NonNull Instant tryStartAt
    ) {
        ExecutionStateValidator.validateRunningPhaseTransition(this, tryStartAt);
        clearResolvedHistory();

        this.phase = ExecutionPhase.RUNNING;
        this.attemptCount++;
        this.tryStartAt = tryStartAt;
    }

    public void markResolved(
            @NonNull Instant resolvedAt,
            @NonNull ExecutionOutput output
    ) {
        ExecutionStateValidator.validateResolvedPhaseTransition(this, resolvedAt, output);

        this.phase = ExecutionPhase.RESOLVED;
        this.resolvedAt = resolvedAt;
        this.output = output;
    }

    public void markExecutionError() {
        ExecutionStateValidator.validateExecutionErrorTransition(this);
        this.phase = ExecutionPhase.EXECUTION_ERROR;
    }

    @Override
    public @NonNull ExecutionPhase phase() {
        return phase;
    }

    @Override
    public int attemptCount() {
        return attemptCount;
    }

    @Override
    public @Nullable Instant tryStartAt() {
        return tryStartAt;
    }

    @Override
    public @Nullable Instant resolvedAt() {
        return resolvedAt;
    }

    @Override
    public @Nullable ExecutionOutput output() {
        return output;
    }

    private void clearResolvedHistory() {
        this.resolvedAt = null;
        this.output = null;
    }
}
