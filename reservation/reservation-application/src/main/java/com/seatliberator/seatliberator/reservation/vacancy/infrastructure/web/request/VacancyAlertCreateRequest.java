package com.seatliberator.seatliberator.reservation.vacancy.infrastructure.web.request;

import java.time.Instant;

public record VacancyAlertCreateRequest(
        String userId,
        String roomId,
        String seatId,
        Instant targetStartTime,
        Instant targetEndTime
) {
}
