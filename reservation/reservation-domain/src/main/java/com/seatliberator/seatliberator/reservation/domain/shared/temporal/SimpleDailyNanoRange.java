package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Duration;
import java.time.LocalTime;

public record SimpleDailyNanoRange(
        long startNanoOfDay,
        long endNanoOfDay
) implements DailyNanoRange {
    public SimpleDailyNanoRange {
        DailyNanoRange.validate(startNanoOfDay, endNanoOfDay);
    }

    public static SimpleDailyNanoRange of(long startNanoOfDay, long endNanoOfDay) {
        return new SimpleDailyNanoRange(startNanoOfDay, endNanoOfDay);
    }

    public static SimpleDailyNanoRange of(long startNanoOfDay, Duration duration) {
        Preconditions.requirePositive(duration, "duration");

        var endNanoOfDay = startNanoOfDay + duration.toNanos();
        return of(startNanoOfDay, endNanoOfDay);
    }

    public static SimpleDailyNanoRange of(LocalTime startAt, Duration duration) {
        Preconditions.requireNonNull(startAt, "startAt");
        Preconditions.requireNonNull(duration, "duration");

        return of(startAt.toNanoOfDay(), duration);
    }

    public static SimpleDailyNanoRange from(DailyNanoRange range) {
        Preconditions.requireNonNull(range, "range");

        return of(range.startNanoOfDay(), range.endNanoOfDay());
    }
}
