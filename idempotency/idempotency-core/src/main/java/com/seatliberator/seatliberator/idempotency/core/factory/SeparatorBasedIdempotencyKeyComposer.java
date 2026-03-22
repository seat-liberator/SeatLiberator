package com.seatliberator.seatliberator.idempotency.core.factory;

import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyKey;
import org.jspecify.annotations.NonNull;

public class SeparatorBasedIdempotencyKeyComposer implements IdempotencyKeyComposer {
    private final String separator;

    public SeparatorBasedIdempotencyKeyComposer(
            @NonNull String separator
    ) {
        if (separator.isBlank()) {
            throw new IllegalArgumentException("separator must be not blank.");
        }

        this.separator = separator;
    }

    @Override
    public @NonNull String compose(@NonNull IdempotencyKey key) {
        if (key.sourceKey().contains(separator)) {
            throw new IllegalArgumentException("sourceKey must not contain separator.");
        }
        if (key.operation().contains(separator)) {
            throw new IllegalArgumentException("operation must not contain separator.");
        }

        return key.sourceKey() + separator + key.operation();
    }
}
