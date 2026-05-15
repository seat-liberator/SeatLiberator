package com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleInstantRange;

import java.util.Objects;

public record ReservationRangeOverlapCriteria(
        SimpleInstantRange range,
        ReservationFilter filter
) {
    public ReservationRangeOverlapCriteria {
        Objects.requireNonNull(range);
        Objects.requireNonNull(filter);
    }

    public static ReservationRangeOverlapCriteria of(InstantRange range) {
        return new ReservationRangeOverlapCriteria(
                SimpleInstantRange.from(range),
                ReservationFilter.empty()
        );
    }

    public ReservationRangeOverlapCriteria withFilter(ReservationFilter filter) {
        return new ReservationRangeOverlapCriteria(range, filter);
    }
}
