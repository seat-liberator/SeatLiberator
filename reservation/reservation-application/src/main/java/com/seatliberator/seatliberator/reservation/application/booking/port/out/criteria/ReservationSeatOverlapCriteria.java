package com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.shared.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleInstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;

import java.util.Objects;

public record ReservationSeatOverlapCriteria(
        SimpleSeatLocator locator,
        SimpleInstantRange range,
        ReservationFilter filter
) {
    public ReservationSeatOverlapCriteria {
        Objects.requireNonNull(locator);
        Objects.requireNonNull(range);
        Objects.requireNonNull(filter);
    }

    public static ReservationSeatOverlapCriteria of(SeatLocator locator, InstantRange range) {
        return new ReservationSeatOverlapCriteria(
                SimpleSeatLocator.from(locator),
                SimpleInstantRange.from(range),
                ReservationFilter.empty()
        );
    }

    public ReservationSeatOverlapCriteria withFilter(ReservationFilter filter) {
        return new ReservationSeatOverlapCriteria(locator, range, filter);
    }
}

