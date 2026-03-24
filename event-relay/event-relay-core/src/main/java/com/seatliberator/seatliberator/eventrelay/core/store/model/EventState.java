package com.seatliberator.seatliberator.eventrelay.core.store.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public interface EventState {
    @NonNull EventStatus status();

    @NonNull Instant acceptedAt();

    @Nullable Instant startedAt();

    @Nullable Instant resolvedAt();

    void markProcessing(@NonNull Instant startedAt);

    void markCompleted(@NonNull Instant resolvedAt);

    void markFailed(@NonNull Instant resolvedAt);

    default boolean isPending() {
        return status() == EventStatus.PENDING;
    }

    default boolean isProcessing() {
        return status() == EventStatus.PROCESSING;
    }

    default boolean isCompleted() {
        return status() == EventStatus.COMPLETED;
    }

    default boolean isFailed() {
        return status() == EventStatus.FAILED;
    }
}
