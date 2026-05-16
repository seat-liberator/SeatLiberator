package com.seatliberator.seatliberator.reservation.application.reservation.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record ListMyReservationQuery(
        String userId
) {
    public ListMyReservationQuery {
        Preconditions.requireNonBlank(userId, "userId");
    }

    public static ListMyReservationQuery of(String userId) {
        return new ListMyReservationQuery(userId);
    }
}
