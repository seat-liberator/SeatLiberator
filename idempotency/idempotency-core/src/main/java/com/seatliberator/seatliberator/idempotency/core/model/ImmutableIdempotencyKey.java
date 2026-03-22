package com.seatliberator.seatliberator.idempotency.core.model;

import org.jspecify.annotations.NonNull;

public record ImmutableIdempotencyKey(
        @NonNull String sourceKey,
        @NonNull String operation
) implements IdempotencyKey {
    public ImmutableIdempotencyKey {
        if (sourceKey.isBlank()) {
            throw new IllegalArgumentException("sourceKey must not be blank.");
        }

        if (operation.isBlank()) {
            throw new IllegalArgumentException("operation must not be blank.");
        }
    }
}
