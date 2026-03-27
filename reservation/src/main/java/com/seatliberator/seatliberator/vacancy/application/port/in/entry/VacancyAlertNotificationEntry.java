package com.seatliberator.seatliberator.vacancy.application.port.in.entry;

import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertNotification;

import java.time.Instant;
import java.util.UUID;

public record VacancyAlertNotificationEntry(
        UUID id,
        UUID vacancyAlertRequestId,
        String userId,
        String roomId,
        String seatId,
        Instant targetStartTime,
        Instant targetEndTime,
        Instant notifiedAt
) {
    public static VacancyAlertNotificationEntry of(VacancyAlertNotification notification) {
        return new VacancyAlertNotificationEntry(
                notification.getId(),
                notification.getVacancyAlertRequestId(),
                notification.getUserId(),
                notification.getRoomId(),
                notification.getSeatId(),
                notification.getTargetStartTime(),
                notification.getTargetEndTime(),
                notification.getNotifiedAt()
        );
    }
}
