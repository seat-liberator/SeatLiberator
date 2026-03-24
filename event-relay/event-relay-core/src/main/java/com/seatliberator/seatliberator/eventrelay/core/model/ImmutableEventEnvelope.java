package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;

public record ImmutableEventEnvelope(
        @NonNull ImmutableEventHeader header,
        @NonNull ImmutableEventTrace trace,
        @NonNull String rawPayload
) implements EventEnvelope {

    public static @NonNull ImmutableEventEnvelope copyOf(@NonNull EventEnvelope envelope) {
        return new ImmutableEventEnvelope(
                ImmutableEventHeader.copyOf(envelope.header()),
                ImmutableEventTrace.copyOf(envelope.trace()),
                envelope.rawPayload()
        );
    }

    public static @NonNull ImmutableEventEnvelope from(
            @NonNull EventHeader header,
            @NonNull EventTrace trace,
            @NonNull String rawPayload
    ) {
        return new ImmutableEventEnvelope(
                ImmutableEventHeader.copyOf(header),
                ImmutableEventTrace.copyOf(trace),
                rawPayload
        );
    }
}
