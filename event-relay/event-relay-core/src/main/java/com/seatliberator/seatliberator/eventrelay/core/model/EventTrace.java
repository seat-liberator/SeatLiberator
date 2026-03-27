package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public interface EventTrace {
    @NonNull String eventId();

    @Nullable String causationId();

    @NonNull String producer();

    @NonNull String correlationId();

    @Nullable EventAggregateDescriptor aggregateDescriptor();

    @NonNull Instant createdAt();
}