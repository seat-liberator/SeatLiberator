package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.LocalDate;

public record SimpleDateRange(
        LocalDate startAt,
        LocalDate endAt
) implements DateRange {
    public SimpleDateRange {
        DateRange.validate(startAt, endAt);
    }

    public static SimpleDateRange of(LocalDate startAt, LocalDate endAt) {
        return new SimpleDateRange(startAt, endAt);
    }

    public static SimpleDateRange from(DateRange range) {
        Preconditions.requireNonNull(range, "range");

        return new SimpleDateRange(range.startAt(), range.endAt());
    }
}
