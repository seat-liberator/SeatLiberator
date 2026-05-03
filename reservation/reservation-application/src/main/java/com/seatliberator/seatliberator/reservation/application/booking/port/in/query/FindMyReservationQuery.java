package com.seatliberator.seatliberator.reservation.application.booking.port.in.query;

import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;

public record FindMyReservationQuery(
        String userId,
        TimeRange range,
        ReservationStatus status
) {
}
