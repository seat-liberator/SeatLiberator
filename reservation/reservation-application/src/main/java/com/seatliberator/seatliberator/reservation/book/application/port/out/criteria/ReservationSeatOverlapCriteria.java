package com.seatliberator.seatliberator.reservation.book.application.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.util.Objects;

public record ReservationSeatOverlapCriteria(
        SimpleSeatLocator locator,
        SimpleTimeRange range,
        ReservationFilter filter
) {
    public ReservationSeatOverlapCriteria {
        Objects.requireNonNull(locator);
        Objects.requireNonNull(range);
        Objects.requireNonNull(filter);
    }

    public static ReservationSeatOverlapCriteria of(SeatLocator locator, TimeRange range) {
        return new ReservationSeatOverlapCriteria(
                SimpleSeatLocator.from(locator),
                SimpleTimeRange.from(range),
                ReservationFilter.empty()
        );
    }

    public ReservationSeatOverlapCriteria withFilter(ReservationFilter filter) {
        return new ReservationSeatOverlapCriteria(locator, range, filter);
    }
}

