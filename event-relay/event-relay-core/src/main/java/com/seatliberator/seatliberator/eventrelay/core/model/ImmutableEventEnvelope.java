package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;

public record ImmutableEventEnvelope(
        @NonNull ImmutableEventHeader header,
        @NonNull ImmutableEventTrace trace,
        @NonNull String rawPayload
) implements EventEnvelope {
    public static ImmutableEventEnvelope copyOf(EventEnvelope envelope) {
        var header = ImmutableEventHeader.copyOf(envelope.header());
        var trace = ImmutableEventTrace.copyOf(envelope.trace());
        return new ImmutableEventEnvelope(
                header,
                trace,
                envelope.rawPayload()
        );
    }
}
