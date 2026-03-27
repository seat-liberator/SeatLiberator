package com.seatliberator.seatliberator.idempotency.core.model;

import org.jspecify.annotations.NonNull;

public record ImmutableIdempotencyContext(
        @NonNull String fingerprint
) implements IdempotencyContext {
    public ImmutableIdempotencyContext {
        if (fingerprint.isBlank()) {
            throw new IllegalArgumentException("fingerprint must not be blank.");
        }
    }

    public static ImmutableIdempotencyContext of(IdempotencyContext context) {
        return new ImmutableIdempotencyContext(context.fingerprint());
    }
}
