package com.seatliberator.seatliberator.reservation.vacancy.infrastructure.web.request;

import java.time.Instant;

public record VacancyAlertCreateRequest(
        String roomId,
        String seatId,
        Instant startAt,
        Instant endAt
) {
}
