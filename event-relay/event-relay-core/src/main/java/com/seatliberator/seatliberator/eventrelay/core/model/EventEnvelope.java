package com.seatliberator.seatliberator.eventrelay.core.model;

import org.jspecify.annotations.NonNull;

public interface EventEnvelope {
    @NonNull EventHeader header();

    @NonNull EventTrace trace();

    @NonNull String rawPayload();
}
