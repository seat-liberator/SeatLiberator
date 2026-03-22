package com.seatliberator.seatliberator.idempotency.core.model;

import org.jspecify.annotations.NonNull;

import java.time.Instant;

public record ImmutableIdempotencyState(
        @NonNull ImmutableIdempotencyKey key,
        @NonNull ImmutableIdempotencyContext context,
        @NonNull ImmutableExecutionState executionState,
        @NonNull Instant createdAt
) implements IdempotencyState {
    public static ImmutableIdempotencyState of(IdempotencyState idempotencyState) {
        return new ImmutableIdempotencyState(
                ImmutableIdempotencyKey.of(idempotencyState.key()),
                ImmutableIdempotencyContext.of(idempotencyState.context()),
                ImmutableExecutionState.of(idempotencyState.executionState()),
                idempotencyState.createdAt()
        );
    }

    public static ImmutableIdempotencyState create(
            IdempotencyKey key,
            IdempotencyContext context,
            Instant createdAt
    ) {
        return new ImmutableIdempotencyState(
                ImmutableIdempotencyKey.of(key),
                ImmutableIdempotencyContext.of(context),
                ImmutableExecutionState.pending(),
                createdAt
        );
    }
}
