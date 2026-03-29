package com.seatliberator.seatliberator.reservation.book.infrastructure.web.request;

import java.time.Instant;

public record ReservationCreateRequest(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime
) {
}
