package com.seatliberator.seatliberator.reservation.book.application.event.payload;

import java.time.Instant;

public record ReservationUsedEvent(
        String roomId,
        String seatId,
        Instant startAt,
        Instant endAt,
        Instant createdAt
) implements BookDomainEventPayload {
}
