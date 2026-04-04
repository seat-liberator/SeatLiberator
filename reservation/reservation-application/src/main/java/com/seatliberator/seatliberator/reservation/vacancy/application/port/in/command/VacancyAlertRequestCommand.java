package com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.time.Instant;

public record VacancyAlertRequestCommand(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime
) {
    public static VacancyAlertRequestCommand from(String userId, SeatLocator locator, TimeRange range) {
        return new VacancyAlertRequestCommand(userId, locator.roomId(), locator.seatId(), range.startAt(), range.endAt());
    }
}
