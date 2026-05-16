package com.seatliberator.seatliberator.reservation.domain.reservation;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationState {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "reserved_at", nullable = false, updatable = false)
    private Instant reservedAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    private ReservationState(
            ReservationStatus status,
            Instant reservedAt,
            Instant usedAt,
            Instant cancelledAt,
            Instant expiredAt
    ) {
        this.status = Preconditions.requireNonNull(status, "status");
        this.reservedAt = Preconditions.requireNonNull(reservedAt, "reservedAt");
        this.usedAt = usedAt;
        this.cancelledAt = cancelledAt;
        this.expiredAt = expiredAt;
    }

    public static ReservationState reservedAt(Instant reservedAt) {
        return new ReservationState(
                ReservationStatus.RESERVED,
                Preconditions.requireNonNull(reservedAt, "reservedAt"),
                null,
                null,
                null
        );
    }

    protected void cancel(Instant at) {
        ensureStateIn(ReservationStatus.RESERVED);

        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = Preconditions.requireNonNull(at, "at");
    }

    protected void expire(Instant at) {
        ensureStateIn(ReservationStatus.RESERVED);

        this.status = ReservationStatus.EXPIRED;
        this.expiredAt = Preconditions.requireNonNull(at, "at");
    }

    protected void use(Instant at) {
        ensureStateIn(ReservationStatus.RESERVED);

        this.status = ReservationStatus.USED;
        this.usedAt = Preconditions.requireNonNull(at, "at");
    }

    private void ensureStateIn(ReservationStatus... statuses) {
        for (var status : statuses) if (this.status == status) return;

        switch (status) {
            case RESERVED -> throw new IllegalStateException("이미 예약되었습니다.");
            case USED -> throw new IllegalStateException("이미 사용된 예약입니다.");
            case CANCELLED -> throw new IllegalStateException("이미 취소된 예약입니다.");
            case EXPIRED -> throw new IllegalStateException("이미 만료된 예약입니다.");
        }
    }
}
