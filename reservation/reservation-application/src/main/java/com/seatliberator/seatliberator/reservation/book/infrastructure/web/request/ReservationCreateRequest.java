package com.seatliberator.seatliberator.reservation.book.infrastructure.web.request;

import java.time.Instant;

public record ReservationCreateRequest(
        Instant startAt,
        Instant endAt
) {
}
