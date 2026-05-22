package com.seatliberator.seatliberator.identity.api.event.payload;

import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;

import java.time.Instant;

public record UserRegisteredEventPayload(
        String userId,
        Instant registeredAt
) implements EventPayload {
}
