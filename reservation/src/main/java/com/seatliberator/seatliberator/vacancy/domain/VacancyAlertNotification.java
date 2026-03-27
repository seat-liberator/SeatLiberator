package com.seatliberator.seatliberator.vacancy.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "vacancy_alert_notification",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vacancy_alert_notification_request",
                        columnNames = {"vacancy_alert_request_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VacancyAlertNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "vacancy_alert_request_id", nullable = false, updatable = false)
    private UUID vacancyAlertRequestId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "room_id", nullable = false, updatable = false)
    private String roomId;

    @Column(name = "seat_id", nullable = false, updatable = false)
    private String seatId;

    @Column(name = "target_start_time", nullable = false, updatable = false)
    private Instant targetStartTime;

    @Column(name = "target_end_time", nullable = false, updatable = false)
    private Instant targetEndTime;

    @Column(name = "notified_at", nullable = false, updatable = false)
    private Instant notifiedAt;

    public static VacancyAlertNotification of(
            @NonNull UUID vacancyAlertRequestId,
            @NonNull String userId,
            @NonNull String roomId,
            @NonNull String seatId,
            @NonNull Instant targetStartTime,
            @NonNull Instant targetEndTime,
            @NonNull Instant notifiedAt
    ) {
        if (!targetStartTime.isBefore(targetEndTime)) {
            throw new IllegalArgumentException("targetStartTime must be before targetEndTime");
        }

        var notification = new VacancyAlertNotification();
        notification.vacancyAlertRequestId = vacancyAlertRequestId;
        notification.userId = userId;
        notification.roomId = roomId;
        notification.seatId = seatId;
        notification.targetStartTime = targetStartTime;
        notification.targetEndTime = targetEndTime;
        notification.notifiedAt = notifiedAt;
        return notification;
    }
}
