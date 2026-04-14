package com.seatliberator.seatliberator.reservation.book.application.port.in.query;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

public record FindMyReservationQuery(
        String userId,
        TimeRange range,
        ReservationStatus status
) {
}
