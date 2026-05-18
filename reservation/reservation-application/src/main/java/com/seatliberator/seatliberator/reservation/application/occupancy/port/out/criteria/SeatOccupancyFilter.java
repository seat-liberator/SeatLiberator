package com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DateRange;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record SeatOccupancyFilter(
        @Nullable UUID reservationId,
        @Nullable DateRange range
) {
    public static SeatOccupancyFilter empty() {
        return new SeatOccupancyFilter(null, null);
    }

    public SeatOccupancyFilter reservationId(UUID reservationId) {
        Preconditions.requireNonNull(reservationId, "reservationId");

        return new SeatOccupancyFilter(reservationId, range);
    }

    public SeatOccupancyFilter range(DateRange range) {
        Preconditions.requireNonNull(range, "range");

        return new SeatOccupancyFilter(reservationId, range);
    }
}