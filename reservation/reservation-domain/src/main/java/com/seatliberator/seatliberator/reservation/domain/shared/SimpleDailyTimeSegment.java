package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Duration;
import java.time.LocalTime;

public record SimpleDailyTimeSegment(
        long startNanoOfDay,
        long endNanoOfDay
) implements DailyTimeSegment {
    public SimpleDailyTimeSegment {
        validate(startNanoOfDay, endNanoOfDay);
    }

    public static SimpleDailyTimeSegment of(Long startNanoOfDay, Long endNanoOfDay) {
        Preconditions.requireNonNull(startNanoOfDay, "startNanoOfDay");
        Preconditions.requireNonNull(endNanoOfDay, "endNanoOfDay");
        return new SimpleDailyTimeSegment(startNanoOfDay, endNanoOfDay);
    }

    public static SimpleDailyTimeSegment of(LocalTime startAt, Duration duration) {
        Preconditions.requireNonNull(startAt, "startAt");
        Preconditions.requireNonNull(duration, "duration");
        var segment = new SimpleDailyTimeSegment(startAt.toNanoOfDay(), startAt.toNanoOfDay() + duration.toNanos());
        segment.validateDuration(duration);
        return segment;
    }

    public static SimpleDailyTimeSegment from(DailyTimeSegment dailyTimeSegment) {
        Preconditions.requireNonNull(dailyTimeSegment, "dailyTimeSegment");
        return of(dailyTimeSegment.startNanoOfDay(), dailyTimeSegment.endNanoOfDay());
    }
}
