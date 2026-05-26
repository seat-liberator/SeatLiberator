package com.seatliberator.seatliberator.reservation.application.seat.port.out.filter;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRange;

public record SeatTimeSlotRangeOverlapCriteria(
        DailyNanoRange range,
        SeatTimeSlotFilter filter
) {
    public SeatTimeSlotRangeOverlapCriteria {
        Preconditions.requireNonNull(range, "range");
        Preconditions.requireNonNull(filter, "filter");
    }

    public static SeatTimeSlotRangeOverlapCriteria of(DailyNanoRange range, SeatTimeSlotFilter filter) {
        return new SeatTimeSlotRangeOverlapCriteria(range, filter);
    }
}
