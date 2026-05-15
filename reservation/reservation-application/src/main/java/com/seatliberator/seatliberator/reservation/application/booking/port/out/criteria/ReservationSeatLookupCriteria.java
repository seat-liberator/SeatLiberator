package com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleInstantRange;

import java.util.Objects;

public record ReservationSeatLookupCriteria(
        SimpleSeatLocator locator,
        SimpleInstantRange range,
        ReservationFilter filter
) {
    public ReservationSeatLookupCriteria {
        Objects.requireNonNull(locator);
        Objects.requireNonNull(range);
        Objects.requireNonNull(filter);
    }

    public static ReservationSeatLookupCriteria of(
            SeatLocator locator,
            InstantRange range
    ) {
        return new ReservationSeatLookupCriteria(
                SimpleSeatLocator.from(locator),
                SimpleInstantRange.from(range),
                ReservationFilter.empty()
        );
    }

    public ReservationSeatLookupCriteria withFilter(ReservationFilter filter) {
        return new ReservationSeatLookupCriteria(locator, range, filter);
    }
}
