package com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleInstantRange;

import java.util.Objects;

public record ReservationRoomOverlapCriteria(
        String roomId,
        SimpleInstantRange range,
        ReservationFilter filter
) {
    public ReservationRoomOverlapCriteria {
        Objects.requireNonNull(roomId);
        Objects.requireNonNull(range);
        Objects.requireNonNull(filter);
    }

    public static ReservationRoomOverlapCriteria of(String roomId, InstantRange range) {
        return new ReservationRoomOverlapCriteria(
                roomId,
                SimpleInstantRange.from(range),
                ReservationFilter.empty()
        );
    }

    public ReservationRoomOverlapCriteria withFilter(ReservationFilter filter) {
        return new ReservationRoomOverlapCriteria(roomId, range, filter);
    }
}
