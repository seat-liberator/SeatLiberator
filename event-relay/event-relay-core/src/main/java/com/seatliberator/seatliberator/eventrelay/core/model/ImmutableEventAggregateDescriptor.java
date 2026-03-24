package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;

public record ImmutableEventAggregateDescriptor(
        @NonNull String type,
        @NonNull String id
) implements EventAggregateDescriptor {

    public static ImmutableEventAggregateDescriptor copyOf(@NonNull EventAggregateDescriptor descriptor) {
        return new ImmutableEventAggregateDescriptor(descriptor.type(), descriptor.id());
    }

    public static ImmutableEventAggregateDescriptor from(
            @NonNull String type,
            @NonNull String id
    ) {
        return new ImmutableEventAggregateDescriptor(type, id);
    }
}
