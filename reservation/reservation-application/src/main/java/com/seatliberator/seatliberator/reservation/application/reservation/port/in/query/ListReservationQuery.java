package com.seatliberator.seatliberator.reservation.application.reservation.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;

public record ListReservationQuery(
        String userId,
        ReservationStatus status
) {
    public ListReservationQuery {
        Preconditions.requireNonBlank(userId, "userId");
        Preconditions.requireNonNull(status, "status");
    }

    public static ListReservationQuery of(String userId, ReservationStatus status) {
        return new ListReservationQuery(userId, status);
    }
}
