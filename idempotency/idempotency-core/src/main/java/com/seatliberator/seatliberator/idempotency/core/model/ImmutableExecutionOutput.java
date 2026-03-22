package com.seatliberator.seatliberator.idempotency.core.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record ImmutableExecutionOutput(
        @Nullable Object result,
        @Nullable Throwable error
) implements ExecutionOutput {
    public ImmutableExecutionOutput {
        if ((result == null) == (error == null)) {
            throw new IllegalArgumentException("Only one of 'result' or 'error' must be present.");
        }
    }

    public static @NonNull ImmutableExecutionOutput of(ExecutionOutput executionOutput) {
        return new ImmutableExecutionOutput(
                executionOutput.result(),
                executionOutput.error()
        );
    }

    public static @NonNull ImmutableExecutionOutput success(@NonNull Object object) {
        return new ImmutableExecutionOutput(object, null);
    }

    public static @NonNull ImmutableExecutionOutput failure(@NonNull Throwable throwable) {
        return new ImmutableExecutionOutput(null, throwable);
    }
}
