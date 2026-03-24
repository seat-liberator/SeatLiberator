package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;

public interface EventAggregateDescriptor {
    @NonNull String type();

    @NonNull String id();
}
