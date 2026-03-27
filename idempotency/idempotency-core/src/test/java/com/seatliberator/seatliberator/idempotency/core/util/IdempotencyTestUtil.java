package com.seatliberator.seatliberator.idempotency.core.util;

import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyContext;
import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyKey;
import com.seatliberator.seatliberator.idempotency.core.model.ImmutableIdempotencyContext;
import com.seatliberator.seatliberator.idempotency.core.model.ImmutableIdempotencyKey;

public class IdempotencyTestUtil {
    public static IdempotencyKey key(String sourceKey, String operation) {
        return new ImmutableIdempotencyKey(sourceKey, operation);
    }

    public static IdempotencyContext context(String fingerprint) {
        return new ImmutableIdempotencyContext(fingerprint);
    }
}
