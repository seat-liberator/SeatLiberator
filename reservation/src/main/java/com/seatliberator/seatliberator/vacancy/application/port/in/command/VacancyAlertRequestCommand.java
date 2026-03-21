package com.seatliberator.seatliberator.vacancy.application.port.in.command;

import java.time.Instant;

public record VacancyAlertRequestCommand(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime,
        Instant requestedAt
) {
}
