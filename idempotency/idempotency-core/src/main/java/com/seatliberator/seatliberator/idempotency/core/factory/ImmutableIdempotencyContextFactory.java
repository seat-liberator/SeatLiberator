package com.seatliberator.seatliberator.idempotency.core.factory;

import com.seatliberator.seatliberator.idempotency.core.model.ImmutableIdempotencyContext;
import org.jspecify.annotations.NonNull;

public class ImmutableIdempotencyContextFactory implements IdempotencyContextFactory<ImmutableIdempotencyContext> {
    @Override
    public @NonNull ImmutableIdempotencyContext create(@NonNull String fingerprint) {
        return new ImmutableIdempotencyContext(fingerprint);
    }
}
