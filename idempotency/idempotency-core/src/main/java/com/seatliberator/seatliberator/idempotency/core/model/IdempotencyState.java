package com.seatliberator.seatliberator.idempotency.core.model;

import org.jspecify.annotations.NonNull;

import java.time.Instant;

public interface IdempotencyState {
    @NonNull IdempotencyKey key();

    @NonNull IdempotencyContext context();

    @NonNull ExecutionState executionState();

    /**
     * 멱등 실행 단위가 최초로 생성된 시각
     */
    @NonNull Instant createdAt();
}
