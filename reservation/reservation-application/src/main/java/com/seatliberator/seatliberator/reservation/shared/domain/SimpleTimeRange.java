package com.seatliberator.seatliberator.reservation.shared.domain;

import java.time.Instant;

public record SimpleTimeRange(
        Instant startAt,
        Instant endAt
) implements TimeRange {
    public SimpleTimeRange {
        if (startAt.isAfter(endAt)) {
            throw new IllegalArgumentException("startAt must be before endAt");
        }
    }

    public static SimpleTimeRange from(Instant startAt, Instant endAt) {
        return new SimpleTimeRange(startAt, endAt);
    }

    public static SimpleTimeRange of(TimeRange range) {
        return new SimpleTimeRange(range.startAt(), range.endAt());
    }
}

