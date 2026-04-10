package com.seatliberator.seatliberator.reservation.book.application.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.util.Objects;

public record ReservationCriteria(
        SeatLocator locator,
        TimeRange range,
        ReservationStatus status
) {
    public ReservationCriteria {
        Objects.requireNonNull(locator);
        Objects.requireNonNull(range);
        Objects.requireNonNull(status);
    }

    public static ReservationCriteria of(
            SeatLocator locator,
            TimeRange range,
            ReservationStatus status
    ) {
        return new ReservationCriteria(locator, range, status);
    }
}
