package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Duration;
import java.time.LocalTime;

public record SimpleDailyTimeSegment(
        long startNanoOfDay,
        long endNanoOfDay
) implements DailyTimeSegment {
    public SimpleDailyTimeSegment {
        DailyTimeSegment.validate(startNanoOfDay, endNanoOfDay);
    }

    public static SimpleDailyTimeSegment of(long startNanoOfDay, long endNanoOfDay) {
        return new SimpleDailyTimeSegment(startNanoOfDay, endNanoOfDay);
    }

    public static SimpleDailyTimeSegment of(long startNanoOfDay, Duration duration) {
        Preconditions.requirePositive(duration, "duration");

        var endNanoOfDay = startNanoOfDay + duration.toNanos();
        return of(startNanoOfDay, endNanoOfDay);
    }

    public static SimpleDailyTimeSegment of(LocalTime startAt, Duration duration) {
        Preconditions.requireNonNull(startAt, "startAt");
        Preconditions.requireNonNull(duration, "duration");

        return of(startAt.toNanoOfDay(), duration);
    }

    public static SimpleDailyTimeSegment from(DailyTimeSegment segment) {
        Preconditions.requireNonNull(segment, "segment");

        return of(segment.startNanoOfDay(), segment.endNanoOfDay());
    }
}
