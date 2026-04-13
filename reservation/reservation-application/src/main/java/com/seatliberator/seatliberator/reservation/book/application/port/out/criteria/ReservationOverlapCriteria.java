package com.seatliberator.seatliberator.reservation.book.application.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.*;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public record ReservationOverlapCriteria(
        SimpleSeatLocator locator,
        SimpleTimeRange range,
        Set<ReservationStatus> statuses,
        Set<Long> excludedIds
) {
    public ReservationOverlapCriteria {
        Objects.requireNonNull(locator);
        Objects.requireNonNull(range);
        statuses = Set.copyOf(Objects.requireNonNull(statuses));
        excludedIds = Set.copyOf(Objects.requireNonNull(excludedIds));
    }

    public static ReservationOverlapCriteria of(SeatLocator locator, TimeRange range) {
        return new ReservationOverlapCriteria(
                SimpleSeatLocator.of(locator),
                SimpleTimeRange.of(range),
                Set.of(),
                Set.of()
        );
    }

    public ReservationOverlapCriteria withStatuses(ReservationStatus... statuses) {
        return new ReservationOverlapCriteria(locator, range, Set.of(statuses), excludedIds);
    }

    public ReservationOverlapCriteria excludedIds(Collection<Long> ids) {
        return new ReservationOverlapCriteria(locator, range, statuses, Set.copyOf(ids));
    }
}

