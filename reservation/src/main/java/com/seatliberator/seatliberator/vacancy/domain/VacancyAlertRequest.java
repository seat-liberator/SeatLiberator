package com.seatliberator.seatliberator.vacancy.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "vacancy_alert_request",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vacancy_alert_active_request",
                        columnNames = {
                                "user_id",
                                "room_id",
                                "seat_id",
                                "target_start_time",
                                "target_end_time"
                        }
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VacancyAlertRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "room_id", nullable = false, updatable = false)
    private String roomId;

    @Column(name = "seat_id", nullable = false, updatable = false)
    private String seatId;

    @Column(name = "target_start_time", nullable = false)
    private Instant targetStartTime;

    @Column(name = "target_end_time", nullable = false)
    private Instant targetEndTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacancyAlertStatus status;

    @Embedded
    private VacancyAlertLifecycle lifecycle;

    public static VacancyAlertRequest of(
            @NonNull String userId,
            @NonNull String roomId,
            @NonNull String seatId,
            @NonNull Instant targetStartTime,
            @NonNull Instant targetEndTime,
            @NonNull Instant requestedAt
    ) {
        if (!targetStartTime.isBefore(targetEndTime)) {
            throw new IllegalArgumentException("targetStartTime is must be before targetEndTime");
        }

        if (targetStartTime.isBefore(Instant.now())){
            throw new IllegalArgumentException("targetStartTime must be future");
        }
        var v = new VacancyAlertRequest();

        v.userId = userId;
        v.roomId = roomId;
        v.seatId = seatId;
        v.targetStartTime = targetStartTime;
        v.targetEndTime = targetEndTime;
        v.status = VacancyAlertStatus.ACTIVE;
        v.lifecycle = VacancyAlertLifecycle.requestedAt(requestedAt);

        return v;
    }

    public void cancel(String requestUserId, Instant cancelledAt) {
        ensureActive();

        // 본인만 신청 취소하도록 함
        if(!this.userId.equals(requestUserId)){
            throw new IllegalArgumentException("본인만 취소 가능");
        }

        this.status = VacancyAlertStatus.CANCELLED;
        this.lifecycle.cancel(cancelledAt);
    }

    public void expire(Instant expiredAt) {
        ensureActive();
        this.status = VacancyAlertStatus.EXPIRED;
        this.lifecycle.expire(expiredAt);
    }

    public void fulfill(Instant fulfilledAt) {
        ensureActive();
        this.status = VacancyAlertStatus.FULFILLED;
        this.lifecycle.fulfill(fulfilledAt);
    }

    private void ensureActive() {
        if (!status.isActive()) throw new IllegalStateException("Only active request can transition");
    }
}
