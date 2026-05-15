package com.seatliberator.seatliberator.reservation.application.booking.port.in.query;

import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;

public record FindMyReservationQuery(
        String userId,
        InstantRange range,
        ReservationStatus status
) {
}
