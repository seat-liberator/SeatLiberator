package com.seatliberator.seatliberator.reservation.book.application.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.util.Objects;

public record ReservationSeatLookupCriteria(
        SimpleSeatLocator locator,
        SimpleTimeRange range,
        ReservationFilter filter
) {
    public ReservationSeatLookupCriteria {
        Objects.requireNonNull(locator);
        Objects.requireNonNull(range);
        Objects.requireNonNull(filter);
    }

    public static ReservationSeatLookupCriteria of(
            SeatLocator locator,
            TimeRange range
    ) {
        return new ReservationSeatLookupCriteria(
                SimpleSeatLocator.from(locator),
                SimpleTimeRange.from(range),
                ReservationFilter.empty()
        );
    }

    public ReservationSeatLookupCriteria withFilter(ReservationFilter filter) {
        return new ReservationSeatLookupCriteria(locator, range, filter);
    }
}
