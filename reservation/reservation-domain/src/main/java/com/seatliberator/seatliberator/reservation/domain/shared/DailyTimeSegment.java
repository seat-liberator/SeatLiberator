package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Duration;
import java.time.LocalTime;

public interface DailyTimeSegment {
    long DAY_NANOS = 24L * 60 * 60 * 1_000_000_000L;

    long startNanoOfDay();

    long endNanoOfDay();

    default void validate(long startNanoOfDay, long endNanoOfDay) {
        if (startNanoOfDay < 0) throw new IllegalArgumentException("startNanoOfDay must not be negative.");
        if (endNanoOfDay > DAY_NANOS) throw new IllegalArgumentException("endNanoOfDay must not be exceed end of day.");
        if (startNanoOfDay >= endNanoOfDay)
            throw new IllegalArgumentException("startNanoOfDay must be before endNanoOfDay.");
    }

    default void validateDuration(Duration duration) {
        if (duration.isZero() || duration.isNegative())
            throw new IllegalArgumentException("duration must be positive.");
        var nanos = duration.toNanos();
        if (nanos > DAY_NANOS)
            throw new IllegalArgumentException("duration must not exceed end of day.");
    }

    default LocalTime startAt() {
        return LocalTime.ofNanoOfDay(startNanoOfDay());
    }

    default Duration duration() {
        return Duration.ofNanos(endNanoOfDay() - startNanoOfDay());
    }

    default boolean contains(LocalTime at) {
        Preconditions.requireNonNull(at, "at");
        return contains(at.toNanoOfDay());
    }

    default boolean contains(long nanoOfDay) {
        if (nanoOfDay < 0 || nanoOfDay >= DAY_NANOS)
            throw new IllegalArgumentException("nanoOfDay must be in range [0, DAY_NANOS).");

        return startNanoOfDay() <= nanoOfDay && nanoOfDay < endNanoOfDay();
    }

    default boolean contains(DailyTimeSegment other) {
        Preconditions.requireNonNull(other, "other");
        return startNanoOfDay() <= other.startNanoOfDay() && other.endNanoOfDay() <= endNanoOfDay();
    }

    default boolean overlaps(DailyTimeSegment other) {
        Preconditions.requireNonNull(other, "other");
        return startNanoOfDay() < other.endNanoOfDay() && other.startNanoOfDay() < endNanoOfDay();
    }

    default boolean isSame(DailyTimeSegment other) {
        Preconditions.requireNonNull(other, "other");
        return startNanoOfDay() == other.startNanoOfDay() && endNanoOfDay() == other.endNanoOfDay();
    }
}