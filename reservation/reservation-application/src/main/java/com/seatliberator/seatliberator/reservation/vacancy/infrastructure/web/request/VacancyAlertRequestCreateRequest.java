package com.seatliberator.seatliberator.reservation.vacancy.infrastructure.web.request;

import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestBehavior;

import java.time.Instant;

public record VacancyAlertRequestCreateRequest(
        String roomId,
        String seatId,
        Instant startAt,
        Instant endAt,
        VacancyAlertRequestBehavior behavior
) {
}
