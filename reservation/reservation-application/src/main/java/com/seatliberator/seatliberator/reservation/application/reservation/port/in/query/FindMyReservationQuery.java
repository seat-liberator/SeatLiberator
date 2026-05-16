package com.seatliberator.seatliberator.reservation.application.reservation.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;

public record FindMyReservationQuery(
        String userId,
        InstantRange range,
        ReservationStatus status
) {
    public FindMyReservationQuery {
        Preconditions.requireNonBlank(userId, "userId");
        Preconditions.requireNonNull(range, "range");
        Preconditions.requireNonNull(status, "status");
    }

    public static FindMyReservationQuery of(String userId, InstantRange range, ReservationStatus status) {
        return new FindMyReservationQuery(userId, range, status);
    }
}
