package com.seatliberator.seatliberator.reservation.book.infrastructure.web.request;

import java.time.Instant;

public record ReservationUpdateRequest(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime
) {
}
