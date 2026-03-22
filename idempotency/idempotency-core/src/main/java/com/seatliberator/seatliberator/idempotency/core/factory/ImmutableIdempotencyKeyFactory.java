package com.seatliberator.seatliberator.idempotency.core.factory;

import com.seatliberator.seatliberator.idempotency.core.model.ImmutableIdempotencyKey;
import org.jspecify.annotations.NonNull;

public class ImmutableIdempotencyKeyFactory implements IdempotencyKeyFactory<ImmutableIdempotencyKey> {
    @Override
    public @NonNull ImmutableIdempotencyKey create(
            @NonNull String sourceKey,
            @NonNull String operation
    ) {
        return new ImmutableIdempotencyKey(sourceKey, operation);
    }
}
