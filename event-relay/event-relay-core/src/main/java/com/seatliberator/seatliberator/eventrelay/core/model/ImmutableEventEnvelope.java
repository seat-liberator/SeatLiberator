package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;

public record ImmutableEventEnvelope(
        @NonNull EventHeader header,
        @NonNull EventTrace trace,
        @NonNull String rawPayload
) implements EventEnvelope {
    public static ImmutableEventEnvelope of(EventEnvelope envelope) {
        var header = ImmutableEventHeader.of(envelope.header());
        var trace = ImmutableEventTrace.of(envelope.trace());
        return new ImmutableEventEnvelope(
                header,
                trace,
                envelope.rawPayload()
        );
    }
}
