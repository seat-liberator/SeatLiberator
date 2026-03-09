package com.seatliberator.seatliberator.reservation.infrastructure.web.request;

import java.time.Instant;

public record ReservationCreateRequest(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime
) {
}
