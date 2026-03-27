package com.seatliberator.seatliberator.idempotency.core.model;

import org.jspecify.annotations.Nullable;

public interface ExecutionOutput {
    @Nullable Object result();

    @Nullable Throwable error();

    default boolean hasResult() {
        return result() != null;
    }

    default boolean hasError() {
        return error() != null;
    }
}
