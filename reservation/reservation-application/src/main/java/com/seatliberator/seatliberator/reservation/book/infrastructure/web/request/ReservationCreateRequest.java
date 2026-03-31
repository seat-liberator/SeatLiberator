package com.seatliberator.seatliberator.reservation.book.infrastructure.web.request;

import java.time.Instant;

public record ReservationCreateRequest(
        String roomId,
        String seatId,
        Instant startAt,
        Instant endAt
) {
}
