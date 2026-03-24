package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;

public record ImmutableEventHeader(
        @NonNull ImmutableEventType eventType
) implements EventHeader {
    public static ImmutableEventHeader copyOf(EventHeader eventHeader) {
        return new ImmutableEventHeader(ImmutableEventType.of(eventHeader.eventType()));
    }
}
