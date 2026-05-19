package com.seatliberator.seatliberator.reservation.application.reservation.port.in.result;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationState;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;

import java.time.Instant;

public record ReservationStateResult(
        ReservationStatus status,
        Instant reservedAt,
        Instant usedAt,
        Instant cancelledAt,
        Instant expiredAt
) {
    public ReservationStateResult {
        Preconditions.requireNonNull(status, "status");
        Preconditions.requireNonNull(reservedAt, "reservedAt");
    }

    public static ReservationStateResult from(ReservationState state) {
        return new ReservationStateResult(
                state.getStatus(),
                state.getReservedAt(),
                state.getUsedAt(),
                state.getCancelledAt(),
                state.getExpiredAt()
        );
    }
}
