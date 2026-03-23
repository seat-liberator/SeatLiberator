package com.seatliberator.seatliberator.idempotency.core.decision;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record ExecutionDecision(
        boolean shouldExecute,
        @NonNull Decision kind,
        @Nullable Throwable throwable
) {
    public ExecutionDecision {
        if (throwable != null && shouldExecute) {
            throw new IllegalArgumentException("shouldExecute must be false when throwable is present");
        }

        if (shouldExecute) {
            if (!(kind == Decision.EXECUTE)) {
                throw new IllegalArgumentException("executable decision must use EXECUTE");
            }
        } else {
            if (kind == Decision.EXECUTE) {
                throw new IllegalArgumentException("non-executing decision cannot use executable decision kind");
            }
        }
    }

    public static @NonNull ExecutionDecision execute() {
        return new ExecutionDecision(true, Decision.EXECUTE, null);
    }

    public static @NonNull ExecutionDecision skip(@NonNull Decision kind) {
        return new ExecutionDecision(false, kind, null);
    }

    public static @NonNull ExecutionDecision reject(
            @NonNull Decision decision,
            @NonNull Throwable throwable
    ) {
        return new ExecutionDecision(false, decision, throwable);
    }
}
