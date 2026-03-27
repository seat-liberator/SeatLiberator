package com.seatliberator.seatliberator.idempotency.core.model;

import com.seatliberator.seatliberator.idempotency.core.util.ExecutionStateValidator;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public record ImmutableExecutionState(
        @NonNull ExecutionPhase phase,
        int attemptCount,
        @Nullable Instant tryStartAt,
        @Nullable Instant resolvedAt,
        @Nullable ExecutionOutput output
) implements ExecutionState {
    public ImmutableExecutionState {
        var snapshot = new ExecutionStateValidator.Snapshot(phase, attemptCount, tryStartAt, resolvedAt, output);
        ExecutionStateValidator.validate(snapshot);
    }

    public static ImmutableExecutionState of(ExecutionState executionState) {
        return new ImmutableExecutionState(
                executionState.phase(),
                executionState.attemptCount(),
                executionState.tryStartAt(),
                executionState.resolvedAt(),
                executionState.output()
        );
    }

    public static ImmutableExecutionState pending() {
        return new ImmutableExecutionState(
                ExecutionPhase.PENDING,
                0,
                null,
                null,
                null
        );
    }

    public static ImmutableExecutionState running(
            int attemptCount,
            @NonNull Instant tryStartAt
    ) {
        return new ImmutableExecutionState(
                ExecutionPhase.RUNNING,
                attemptCount,
                tryStartAt,
                null,
                null
        );
    }

    public static ImmutableExecutionState resolved(
            int attemptCount,
            @NonNull Instant tryStartAt,
            @NonNull Instant resolvedAt,
            @NonNull ExecutionOutput output
    ) {
        return new ImmutableExecutionState(
                ExecutionPhase.RESOLVED,
                attemptCount,
                tryStartAt,
                resolvedAt,
                output
        );
    }

    public static ImmutableExecutionState executionError(
            int attemptCount,
            @NonNull Instant tryStartAt
    ) {
        return new ImmutableExecutionState(
                ExecutionPhase.EXECUTION_ERROR,
                attemptCount,
                tryStartAt,
                null,
                null
        );
    }

    public ImmutableExecutionState markRunning(@NonNull Instant tryStartAt) {
        ExecutionStateValidator.validateRunningPhaseTransition(this, tryStartAt);

        return new ImmutableExecutionState(
                ExecutionPhase.RUNNING,
                attemptCount + 1,
                tryStartAt,
                null,
                null
        );
    }

    public ImmutableExecutionState markResolved(
            @NonNull Instant resolvedAt,
            @NonNull ExecutionOutput output
    ) {
        ExecutionStateValidator.validateResolvedPhaseTransition(this, resolvedAt, output);

        return new ImmutableExecutionState(
                ExecutionPhase.RESOLVED,
                attemptCount,
                tryStartAt,
                resolvedAt,
                output
        );
    }

    public ImmutableExecutionState markExecutionError() {
        ExecutionStateValidator.validateExecutionErrorTransition(this);

        return new ImmutableExecutionState(
                ExecutionPhase.EXECUTION_ERROR,
                attemptCount,
                tryStartAt,
                null,
                null
        );
    }
}
