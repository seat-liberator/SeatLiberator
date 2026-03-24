package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;

public record ImmutableEventType(
        @NonNull String name
) implements EventType {
    public static ImmutableEventType of(EventType type) {
        return new ImmutableEventType(type.name());
    }
}
