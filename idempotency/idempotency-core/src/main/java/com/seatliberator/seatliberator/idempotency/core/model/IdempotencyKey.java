package com.seatliberator.seatliberator.idempotency.core.model;

import org.jspecify.annotations.NonNull;

public interface IdempotencyKey {
    @NonNull String sourceKey();

    @NonNull String operation();
}
