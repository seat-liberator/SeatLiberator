package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;

public record ImmutableEventTrace(
        @NonNull String eventId,
        @Nullable String causationId,
        @NonNull String producer,
        @NonNull String correlationId,
        @Nullable ImmutableEventAggregateDescriptor aggregateDescriptor,
        @NonNull Instant createdAt
) implements EventTrace {
    public static ImmutableEventTrace copyOf(EventTrace trace) {
        var descriptor = Optional.ofNullable(trace.aggregateDescriptor())
                .map(ImmutableEventAggregateDescriptor::of)
                .orElse(null);

        return new ImmutableEventTrace(
                trace.eventId(),
                trace.causationId(),
                trace.producer(),
                trace.correlationId(),
                descriptor,
                trace.createdAt()
        );
    }
}
