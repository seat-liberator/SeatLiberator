package com.seatliberator.seatliberator.idempotency.core.factory;

import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyKey;
import org.jspecify.annotations.NonNull;

public interface IdempotencyKeyFactory<K extends IdempotencyKey> {
    @NonNull K create(
            @NonNull String sourceKey,
            @NonNull String operation
    );
}
