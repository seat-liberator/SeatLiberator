package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
                                "target_room_id",
                                "target_seat_id",
                                "target_start_at",
                                "target_end_at"
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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "roomId", column = @Column(name = "target_room_id")),
            @AttributeOverride(name = "seatId", column = @Column(name = "target_seat_id"))
    })
    private EmbeddableSeatLocator locator;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startAt", column = @Column(name = "target_start_at")),
            @AttributeOverride(name = "endAt", column = @Column(name = "target_end_at"))
    })
    private EmbeddableTimeRange range;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacancyAlertStatus status;

    @Embedded
    private VacancyAlertLifecycle lifecycle;

    public static VacancyAlertRequest create(
            String userId,
            SeatLocator locator,
            TimeRange range,
            Instant requestedAt
    ) {
        if (!requestedAt.isBefore(range.startAt())) {
            throw new IllegalArgumentException("requestedAt is must be before startAt");
        }

        var v = new VacancyAlertRequest();

        v.userId = userId;
        v.locator = EmbeddableSeatLocator.of(locator);
        v.range = EmbeddableTimeRange.of(range);
        v.status = VacancyAlertStatus.ACTIVE;
        v.lifecycle = VacancyAlertLifecycle.requestedAt(requestedAt);

        return v;
    }

    public void cancel(String requestUserId, Instant cancelledAt) {
        ensureActive();

        // 본인만 신청 취소하도록 함
        if (!this.userId.equals(requestUserId)) {
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
