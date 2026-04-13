package com.seatliberator.seatliberator.reservation.book.application.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.*;

import java.util.Objects;
import java.util.Set;

public record ReservationFindOneCriteria(
        SimpleSeatLocator locator,
        SimpleTimeRange range,
        Set<ReservationStatus> statuses
) {
    public ReservationFindOneCriteria {
        Objects.requireNonNull(locator);
        Objects.requireNonNull(range);
        statuses = Set.copyOf(Objects.requireNonNull(statuses));
    }

    public static ReservationFindOneCriteria of(
            SeatLocator locator,
            TimeRange range
    ) {
        return new ReservationFindOneCriteria(
                SimpleSeatLocator.of(locator),
                SimpleTimeRange.of(range),
                Set.of()
        );
    }

    public ReservationFindOneCriteria withStatuses(ReservationStatus... statuses) {
        return new ReservationFindOneCriteria(locator, range, Set.of(statuses));
    }
}
