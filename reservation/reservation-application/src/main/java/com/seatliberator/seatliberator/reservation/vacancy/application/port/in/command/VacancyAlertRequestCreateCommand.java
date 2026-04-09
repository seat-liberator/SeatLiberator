package com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestBehavior;

import java.time.Instant;

public record VacancyAlertRequestCreateCommand(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime,
        VacancyAlertRequestBehavior behavior
) {
    public static VacancyAlertRequestCreateCommand from(String userId, SeatLocator locator, TimeRange range, VacancyAlertRequestBehavior behavior) {
        return new VacancyAlertRequestCreateCommand(userId, locator.roomId(), locator.seatId(), range.startAt(), range.endAt(), behavior);
    }
}
