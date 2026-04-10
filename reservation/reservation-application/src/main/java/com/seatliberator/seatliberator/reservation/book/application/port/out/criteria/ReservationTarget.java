package com.seatliberator.seatliberator.reservation.book.application.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.util.Objects;

public record ReservationTarget(
        SeatLocator locator,
        TimeRange ranga
) {
    public ReservationTarget {
        Objects.requireNonNull(locator);
        Objects.requireNonNull(ranga);
    }

    public static ReservationTarget of(SeatLocator locator, TimeRange range) {
        return new ReservationTarget(locator, range);
    }
}
