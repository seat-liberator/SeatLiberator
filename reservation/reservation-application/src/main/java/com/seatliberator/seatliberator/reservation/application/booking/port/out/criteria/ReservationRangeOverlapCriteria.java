package com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.shared.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;

import java.util.Objects;

public record ReservationRangeOverlapCriteria(
        SimpleTimeRange range,
        ReservationFilter filter
) {
    public ReservationRangeOverlapCriteria {
        Objects.requireNonNull(range);
        Objects.requireNonNull(filter);
    }

    public static ReservationRangeOverlapCriteria of(TimeRange range) {
        return new ReservationRangeOverlapCriteria(
                SimpleTimeRange.from(range),
                ReservationFilter.empty()
        );
    }

    public ReservationRangeOverlapCriteria withFilter(ReservationFilter filter) {
        return new ReservationRangeOverlapCriteria(range, filter);
    }
}
