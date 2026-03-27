package com.seatliberator.seatliberator.vacancy.application.port.in.command;

import java.time.Instant;
import java.util.UUID;

public record VacancyAlertNotificationCreateCommand(
        UUID vacancyAlertRequestId,
        String userId,
        String roomId,
        String seatId,
        Instant targetStartTime,
        Instant targetEndTime,
        Instant notifiedAt
) {
}
