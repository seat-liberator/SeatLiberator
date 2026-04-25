package com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.util.Objects;

public record ReservationRoomOverlapCriteria(
        String roomId,
        SimpleTimeRange range,
        ReservationFilter filter
) {
    public ReservationRoomOverlapCriteria {
        Objects.requireNonNull(roomId);
        Objects.requireNonNull(range);
        Objects.requireNonNull(filter);
    }

    public static ReservationRoomOverlapCriteria of(String roomId, TimeRange range) {
        return new ReservationRoomOverlapCriteria(
                roomId,
                SimpleTimeRange.from(range),
                ReservationFilter.empty()
        );
    }

    public ReservationRoomOverlapCriteria withFilter(ReservationFilter filter) {
        return new ReservationRoomOverlapCriteria(roomId, range, filter);
    }
}
