package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;

public record ImmutableEventType(
        @NonNull String name
) implements EventType {

    public static @NonNull ImmutableEventType copyOf(@NonNull EventType type) {
        return new ImmutableEventType(type.name());
    }

    public static @NonNull ImmutableEventType from(@NonNull String name) {
        return new ImmutableEventType(name);
    }
}
