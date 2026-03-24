package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;

public interface EventHeader {
    @NonNull EventType eventType();
}
