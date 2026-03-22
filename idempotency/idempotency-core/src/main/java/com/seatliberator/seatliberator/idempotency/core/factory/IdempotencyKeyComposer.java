package com.seatliberator.seatliberator.idempotency.core.factory;

import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyKey;
import org.jspecify.annotations.NonNull;

public interface IdempotencyKeyComposer {
    @NonNull String compose(@NonNull IdempotencyKey key);
}
