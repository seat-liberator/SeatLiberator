package com.seatliberator.seatliberator.idempotency.core.util;

import com.seatliberator.seatliberator.idempotency.core.model.ExecutionOutput;
import com.seatliberator.seatliberator.idempotency.core.model.ExecutionPhase;
import com.seatliberator.seatliberator.idempotency.core.model.ExecutionState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public final class ExecutionStateValidator {
    public static void validate(@NonNull ExecutionState state) {
        Snapshot snapshot = Snapshot.of(state);
        validate(snapshot);
    }

    public static void validate(@NonNull Snapshot snapshot) {
        switch (snapshot.phase()) {
            case PENDING -> validatePendingPhase(snapshot);
            case RUNNING -> validateRunningPhase(snapshot);
            case RESOLVED -> validateResolvedPhase(snapshot);
            case EXECUTION_ERROR -> validateExecutionErrorPhase(snapshot);
        }
    }

    public static void validatePendingPhase(@NonNull ExecutionState state) {
        Snapshot snapshot = Snapshot.of(state);
        validatePendingPhase(snapshot);
    }

    public static void validatePendingPhase(@NonNull Snapshot snapshot) {
        if (snapshot.attemptCount() != 0) {
            throw new IllegalArgumentException("PENDING phase must have attemptCount == 0.");
        }
        if (snapshot.tryStartAt() != null) {
            throw new IllegalArgumentException("PENDING phase must not have tryStartAt.");
        }
        if (snapshot.resolvedAt() != null) {
            throw new IllegalArgumentException("PENDING phase must not have resolvedAt.");
        }
        if (snapshot.output() != null) {
            throw new IllegalArgumentException("PENDING phase must not have output.");
        }
    }

    public static void validateRunningPhaseTransition(
            @NonNull ExecutionState state,
            @NonNull Instant tryStartAt
    ) {
        Snapshot snapshot = Snapshot.of(state);
        validateRunningPhaseTransition(snapshot, tryStartAt);
    }

    public static void validateRunningPhase(@NonNull ExecutionState state) {
        Snapshot snapshot = Snapshot.of(state);
        validateRunningPhase(snapshot);
    }

    public static void validateRunningPhaseTransition(
            @NonNull Snapshot snapshot,
            @NonNull Instant tryStartAt
    ) {
        if (!(snapshot.phase() == ExecutionPhase.PENDING || snapshot.phase() == ExecutionPhase.EXECUTION_ERROR)) {
            throw new IllegalStateException("Can transition to RUNNING only when phase is PENDING or EXECUTION_ERROR");
        }

        Snapshot transition = new Snapshot(
                ExecutionPhase.RUNNING,
                snapshot.attemptCount() + 1,
                tryStartAt,
                null,
                null
        );

        validateRunningPhase(transition);
    }

    public static void validateRunningPhase(@NonNull Snapshot snapshot) {
        if (snapshot.attemptCount() <= 0) {
            throw new IllegalArgumentException("RUNNING phase must have attemptCount > 0.");
        }
        if (snapshot.tryStartAt() == null) {
            throw new IllegalArgumentException("RUNNING phase must have tryStartAt.");
        }
        if (snapshot.resolvedAt() != null) {
            throw new IllegalArgumentException("RUNNING phase must not have resolvedAt.");
        }
        if (snapshot.output() != null) {
            throw new IllegalArgumentException("RUNNING phase must not have output.");
        }
    }

    public static void validateResolvedPhaseTransition(
            @NonNull ExecutionState state,
            @NonNull Instant resolvedAt,
            @NonNull ExecutionOutput output
    ) {
        Snapshot snapshot = Snapshot.of(state);
        validateResolvedPhaseTransition(snapshot, resolvedAt, output);
    }

    public static void validateResolvedPhase(@NonNull ExecutionState state) {
        Snapshot snapshot = Snapshot.of(state);
        validateResolvedPhase(snapshot);
    }

    public static void validateResolvedPhaseTransition(
            @NonNull Snapshot snapshot,
            @NonNull Instant resolvedAt,
            @NonNull ExecutionOutput output
    ) {
        if (snapshot.phase() != ExecutionPhase.RUNNING) {
            throw new IllegalStateException("Can transition to RESOLVED only when phase is RUNNING.");
        }

        Snapshot transition = new Snapshot(
                ExecutionPhase.RESOLVED,
                snapshot.attemptCount(),
                snapshot.tryStartAt(),
                resolvedAt,
                output
        );

        validateResolvedPhase(transition);
    }

    public static void validateResolvedPhase(@NonNull Snapshot snapshot) {
        if (snapshot.tryStartAt() == null) {
            throw new IllegalArgumentException("RESOLVED phase must have tryStartAt.");
        }
        if (snapshot.resolvedAt() == null) {
            throw new IllegalArgumentException("RESOLVED phase must have resolvedAt.");
        }
        if (snapshot.resolvedAt().isBefore(snapshot.tryStartAt())) {
            throw new IllegalArgumentException("resolvedAt must not be before tryStartAt.");
        }
        if (snapshot.attemptCount() <= 0) {
            throw new IllegalArgumentException("RESOLVED phase must have attemptCount > 0.");
        }
        if (snapshot.output() == null) {
            throw new IllegalArgumentException("RESOLVED phase must have output.");
        }
    }

    public static void validateExecutionErrorTransition(@NonNull ExecutionState state) {
        Snapshot snapshot = Snapshot.of(state);
        validateExecutionErrorTransition(snapshot);
    }

    public static void validateExecutionErrorPhase(@NonNull ExecutionState state) {
        Snapshot snapshot = Snapshot.of(state);
        validateExecutionErrorPhase(snapshot);
    }

    public static void validateExecutionErrorTransition(@NonNull Snapshot snapshot) {
        if (snapshot.phase() != ExecutionPhase.RUNNING) {
            throw new IllegalStateException("Can transition to EXECUTION_ERROR only when phase is RUNNING");
        }

        Snapshot transition = new Snapshot(
                ExecutionPhase.EXECUTION_ERROR,
                snapshot.attemptCount(),
                snapshot.tryStartAt(),
                null,
                null
        );

        validateExecutionErrorPhase(transition);
    }

    public static void validateExecutionErrorPhase(@NonNull Snapshot snapshot) {
        if (snapshot.attemptCount() <= 0) {
            throw new IllegalArgumentException("EXECUTION_ERROR phase must have attemptCount > 0.");
        }
        if (snapshot.tryStartAt() == null) {
            throw new IllegalArgumentException("EXECUTION_ERROR phase must have tryStartAt.");
        }
        if (snapshot.resolvedAt() != null) {
            throw new IllegalArgumentException("EXECUTION_ERROR phase must not have resolvedAt.");
        }
        if (snapshot.output() != null) {
            throw new IllegalArgumentException("EXECUTION_ERROR phase must not have output.");
        }
    }

    public record Snapshot(
            @NonNull ExecutionPhase phase,
            int attemptCount,
            @Nullable Instant tryStartAt,
            @Nullable Instant resolvedAt,
            @Nullable ExecutionOutput output
    ) {
        public static Snapshot of(ExecutionState state) {
            return new Snapshot(
                    state.phase(),
                    state.attemptCount(),
                    state.tryStartAt(),
                    state.resolvedAt(),
                    state.output()
            );
        }
    }
}
