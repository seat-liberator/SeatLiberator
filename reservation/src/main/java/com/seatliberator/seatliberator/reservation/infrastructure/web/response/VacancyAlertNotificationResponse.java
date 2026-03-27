package com.seatliberator.seatliberator.reservation.infrastructure.web.response;

import com.seatliberator.seatliberator.vacancy.application.port.in.entry.VacancyAlertNotificationEntry;

import java.time.Instant;
import java.util.UUID;

public record VacancyAlertNotificationResponse(
        UUID notificationId,
        UUID vacancyAlertRequestId,
        String roomId,
        String seatId,
        Instant targetStartTime,
        Instant targetEndTime,
        Instant notifiedAt
) {
    public static VacancyAlertNotificationResponse from(VacancyAlertNotificationEntry entry) {
        return new VacancyAlertNotificationResponse(
                entry.id(),
                entry.vacancyAlertRequestId(),
                entry.roomId(),
                entry.seatId(),
                entry.targetStartTime(),
                entry.targetEndTime(),
                entry.notifiedAt()
        );
    }
}
