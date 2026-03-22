package com.seatliberator.seatliberator.idempotency.core.model;

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

        validatePhase();
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
        if (!(phase == ExecutionPhase.PENDING || phase == ExecutionPhase.EXECUTION_ERROR)) {
            throw new IllegalStateException("Can transition to RUNNING only when phase is PENDING or EXECUTION_ERROR");
        }

        this.phase = ExecutionPhase.RUNNING;
        this.attemptCount++;
        this.tryStartAt = tryStartAt;

        clearResolvedHistory();
        validateRunningPhase();
    }

    public void markResolved(
            @NonNull Instant resolvedAt,
            @NonNull ExecutionOutput output
    ) {
        if (phase != ExecutionPhase.RUNNING) {
            throw new IllegalStateException("Can transition to RESOLVED only when phase is RUNNING.");
        }

        this.phase = ExecutionPhase.RESOLVED;
        this.resolvedAt = resolvedAt;
        this.output = output;

        validateResolvedPhase();
    }

    public void markExecutionError() {
        if (phase != ExecutionPhase.RUNNING) {
            throw new IllegalStateException("Can transition to EXECUTION_ERROR only when phase is RUNNING");
        }
        this.phase = ExecutionPhase.EXECUTION_ERROR;

        clearResolvedHistory();
        validateExecutionErrorPhase();
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

    private void validatePhase() {
        switch (phase) {
            case PENDING -> validatePendingPhase();
            case RUNNING -> validateRunningPhase();
            case RESOLVED -> validateResolvedPhase();
            case EXECUTION_ERROR -> validateExecutionErrorPhase();
        }
    }

    private void validatePendingPhase() {
        if (attemptCount != 0) {
            throw new IllegalArgumentException("PENDING phase must have attemptCount == 0.");
        }

        if (tryStartAt != null) {
            throw new IllegalArgumentException("PENDING phase must not have tryStartAt.");
        }
        if (resolvedAt != null) {
            throw new IllegalArgumentException("PENDING phase must not have resolvedAt.");
        }
        if (output != null) {
            throw new IllegalArgumentException("PENDING phase must not have output.");
        }
    }

    private void validateRunningPhase() {
        if (attemptCount <= 0) {
            throw new IllegalArgumentException("RUNNING phase must have attemptCount > 0.");
        }

        if (tryStartAt == null) {
            throw new IllegalArgumentException("RUNNING phase must have tryStartAt.");
        }
        if (resolvedAt != null) {
            throw new IllegalArgumentException("RUNNING phase must not have resolvedAt.");
        }
        if (output != null) {
            throw new IllegalArgumentException("RUNNING phase must not have output.");
        }
    }

    private void validateResolvedPhase() {
        if (attemptCount <= 0) {
            throw new IllegalArgumentException("RESOLVED phase must have attemptCount > 0.");
        }

        if (tryStartAt == null) {
            throw new IllegalArgumentException("RESOLVED phase must have tryStartAt.");
        }
        if (resolvedAt == null) {
            throw new IllegalArgumentException("RESOLVED phase must have resolvedAt.");
        }
        if (output == null) {
            throw new IllegalArgumentException("RESOLVED phase must have output.");
        }
        if (resolvedAt.isBefore(tryStartAt)) {
            throw new IllegalArgumentException("resolvedAt must not be before tryStartAt.");
        }
    }

    private void validateExecutionErrorPhase() {
        if (attemptCount <= 0) {
            throw new IllegalArgumentException("EXECUTION_ERROR phase must have attemptCount > 0.");
        }

        if (tryStartAt == null) {
            throw new IllegalArgumentException("EXECUTION_ERROR phase must have tryStartAt.");
        }
        if (resolvedAt != null) {
            throw new IllegalArgumentException("EXECUTION_ERROR phase must not have resolvedAt.");
        }
        if (output != null) {
            throw new IllegalArgumentException("EXECUTION_ERROR phase must not have output.");
        }
    }
}
