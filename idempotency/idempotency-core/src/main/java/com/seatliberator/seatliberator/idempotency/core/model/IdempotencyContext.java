package com.seatliberator.seatliberator.idempotency.core.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface IdempotencyContext {
    @NonNull String fingerprint();

    default boolean sameContext(@Nullable IdempotencyContext other) {
        return other != null && fingerprint().equals(other.fingerprint());
    }
}
