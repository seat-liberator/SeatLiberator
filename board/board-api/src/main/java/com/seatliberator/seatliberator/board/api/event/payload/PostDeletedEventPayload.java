package com.seatliberator.seatliberator.board.api.event.payload;

import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;

import java.time.Instant;

public record PostDeletedEventPayload(
        String id,
        Instant deletedAt
) implements EventPayload {
}
