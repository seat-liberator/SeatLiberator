package com.seatliberator.seatliberator.reservation.application.availability.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;

import java.time.Instant;

public record SeatOccupancyRangeResult(
        Instant startAt,
        Instant endAt
) {
    public static SeatOccupancyRangeResult of(TimeRange range) {
        return new SeatOccupancyRangeResult(range.startAt(), range.endAt());
    }
}
