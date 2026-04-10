package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "waitlist")
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
    @Column(name = "behavior", nullable = false)
    private VacancyAlertRequestBehavior behavior;

    @Embedded
    private VacancyAlertRequestState state;

    private VacancyAlertRequest(
            String userId,
            EmbeddableSeatLocator locator,
            EmbeddableTimeRange range,
            VacancyAlertRequestBehavior behavior,
            VacancyAlertRequestState state
    ) {
        if (!state.getRequestedAt().isBefore(range.startAt())) {
            throw new IllegalArgumentException("requestedAt is must be before startAt");
        }

        this.userId = Objects.requireNonNull(userId);
        this.locator = Objects.requireNonNull(locator);
        this.range = Objects.requireNonNull(range);
        this.behavior = Objects.requireNonNull(behavior);
        this.state = Objects.requireNonNull(state);
    }

    public static VacancyAlertRequest create(
            String userId,
            SeatLocator locator,
            TimeRange range,
            VacancyAlertRequestBehavior actionType,
            Instant requestedAt
    ) {
        return new VacancyAlertRequest(
                userId,
                EmbeddableSeatLocator.of(locator),
                EmbeddableTimeRange.of(range),
                actionType,
                VacancyAlertRequestState.requestedAt(requestedAt)
        );
    }

    public static VacancyAlertRequest notifyOnly(
            String userId,
            SeatLocator locator,
            TimeRange range,
            Instant requestedAt
    ) {
        return create(userId, locator, range, VacancyAlertRequestBehavior.NOTIFY_ONLY, requestedAt);
    }

    public static VacancyAlertRequest autoClaim(
            String userId,
            SeatLocator locator,
            TimeRange range,
            Instant requestedAt
    ) {
        return create(userId, locator, range, VacancyAlertRequestBehavior.AUTO_CLAIM, requestedAt);
    }

    public void cancel(Instant cancelledAt) {
//        // 본인만 신청 취소하도록 함
//        // TODO: 이거는 나중에 애플리케이션 레벨 정책으로 빼는 방향이 좋을 것 같음.
//        // 만약 관리자가 대기열을 건드려야한다면?
//        if (!this.userId.equals(requestUserId)) {
//            throw new IllegalArgumentException("본인만 취소 가능");
//        }

        this.state.cancel(cancelledAt);
    }

    public void expire(Instant expiredAt) {
        this.state.expire(expiredAt);
    }

    public void fail(Instant failedAt) {
        this.state.fail(failedAt);
    }

    public void complete(Instant completedAt) {
        switch (behavior) {
            case NOTIFY_ONLY -> state.completeAsNotified(completedAt);
            case AUTO_CLAIM -> state.completeAtClaimed(completedAt);
        }
    }
}
