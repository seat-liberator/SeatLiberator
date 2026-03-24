package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;

public record ImmutableEventHeader(
        @NonNull ImmutableEventType eventType
) implements EventHeader {

    public static @NonNull ImmutableEventHeader copyOf(@NonNull EventHeader eventHeader) {
        return new ImmutableEventHeader(ImmutableEventType.copyOf(eventHeader.eventType()));
    }

    public static @NonNull ImmutableEventHeader from(@NonNull EventType type) {
        return new ImmutableEventHeader(
                ImmutableEventType.copyOf(type)
        );
    }
}
