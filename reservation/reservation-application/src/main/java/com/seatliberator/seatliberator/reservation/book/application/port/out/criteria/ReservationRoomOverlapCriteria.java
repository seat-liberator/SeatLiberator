package com.seatliberator.seatliberator.reservation.book.application.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.util.Objects;
import java.util.Set;

public record ReservationRoomOverlapCriteria(
        String roomId,
        SimpleTimeRange range,
        Set<ReservationStatus> statuses
) {
    public ReservationRoomOverlapCriteria {
        Objects.requireNonNull(roomId);
        Objects.requireNonNull(range);
        statuses = Objects.requireNonNull(Set.copyOf(statuses));
    }

    public static ReservationRoomOverlapCriteria of(String roomId, TimeRange range) {
        return new ReservationRoomOverlapCriteria(
                roomId,
                SimpleTimeRange.of(range),
                Set.of()
        );
    }

    public ReservationRoomOverlapCriteria withStatuses(ReservationStatus... statuses) {
        return new ReservationRoomOverlapCriteria(roomId, range, Set.of(statuses));
    }
}
