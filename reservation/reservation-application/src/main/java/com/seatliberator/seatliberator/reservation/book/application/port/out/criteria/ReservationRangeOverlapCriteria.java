package com.seatliberator.seatliberator.reservation.book.application.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

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
                SimpleTimeRange.of(range),
                ReservationFilter.empty()
        );
    }

    public ReservationRangeOverlapCriteria withFilter(ReservationFilter filter) {
        return new ReservationRangeOverlapCriteria(range, filter);
    }
}
