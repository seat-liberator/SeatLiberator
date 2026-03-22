package com.seatliberator.seatliberator.idempotency.core.factory;

import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyContext;
import org.jspecify.annotations.NonNull;

public interface IdempotencyContextFactory<C extends IdempotencyContext> {
    @NonNull C create(@NonNull String fingerprint);
}
