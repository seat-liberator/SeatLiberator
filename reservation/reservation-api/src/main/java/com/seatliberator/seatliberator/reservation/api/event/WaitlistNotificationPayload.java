package com.seatliberator.seatliberator.reservation.api.event;

import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;

import java.time.Instant;

public record WaitlistNotificationPayload(
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime
) implements EventPayload {
}
