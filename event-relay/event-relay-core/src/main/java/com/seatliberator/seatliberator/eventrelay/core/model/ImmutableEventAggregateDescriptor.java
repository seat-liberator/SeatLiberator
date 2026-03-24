package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;

public record ImmutableEventAggregateDescriptor(
        @NonNull String type,
        @NonNull String id
) implements EventAggregateDescriptor {
    public static ImmutableEventAggregateDescriptor of(EventAggregateDescriptor descriptor) {
        return new ImmutableEventAggregateDescriptor(descriptor.type(), descriptor.id());
    }
}
