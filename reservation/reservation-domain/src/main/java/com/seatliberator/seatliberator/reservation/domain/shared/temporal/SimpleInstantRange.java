package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Instant;

public record SimpleInstantRange(
        Instant startAt,
        Instant endAt
) implements InstantRange {
    public SimpleInstantRange {
        InstantRange.validate(startAt, endAt);
    }

    public static SimpleInstantRange of(Instant startAt, Instant endAt) {
        return new SimpleInstantRange(startAt, endAt);
    }

    public static SimpleInstantRange from(InstantRange range) {
        Preconditions.requireNonNull(range, "range");

        return new SimpleInstantRange(range.startAt(), range.endAt());
    }
}
