package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public interface DailyNanoRange extends RangeComparable<DailyNanoRange> {
    long DAY_NANOS = 24L * 60 * 60 * 1_000_000_000L;

    static void validate(long startNanoOfDay, long endNanoOfDay) {
        Preconditions.requireBetween(startNanoOfDay, 0L, DAY_NANOS - 1, "startNanoOfDay");
        Preconditions.requireBetween(endNanoOfDay, 1L, DAY_NANOS, "endNanoOfDay");

        if (startNanoOfDay >= endNanoOfDay)
            throw new IllegalArgumentException("startNanoOfDay must be before endNanoOfDay.");
    }

    long startNanoOfDay();

    long endNanoOfDay();

    default LocalTime startAt() {
        return LocalTime.ofNanoOfDay(startNanoOfDay());
    }

    default Duration duration() {
        return Duration.ofNanos(endNanoOfDay() - startNanoOfDay());
    }

    default boolean contains(long other) {
        if (other < 0 || other >= DAY_NANOS)
            throw new IllegalArgumentException("nanoOfDay must be between 0 and " + (DAY_NANOS - 1) + ".");

        return startNanoOfDay() <= other && other < endNanoOfDay();
    }

    default boolean contains(LocalTime other) {
        Preconditions.requireNonNull(other, "other");
        return contains(other.toNanoOfDay());
    }

    default boolean contains(Instant other, ZoneId zoneId) {
        Preconditions.requireNonNull(other, "other");
        Preconditions.requireNonNull(zoneId, "zoneId");

        var targetNanoOfDay = other.atZone(zoneId).toLocalTime().toNanoOfDay();

        return contains(targetNanoOfDay);
    }

    @Override
    default boolean isSame(DailyNanoRange other) {
        Preconditions.requireNonNull(other, "other");

        return startNanoOfDay() == other.startNanoOfDay() && endNanoOfDay() == other.endNanoOfDay();
    }

    @Override
    default boolean startsBefore(DailyNanoRange other) {
        Preconditions.requireNonNull(other, "other");

        return startNanoOfDay() < other.startNanoOfDay();
    }

    @Override
    default boolean endsAfter(DailyNanoRange other) {
        Preconditions.requireNonNull(other, "other");

        return endNanoOfDay() > other.endNanoOfDay();
    }

    @Override
    default boolean contains(DailyNanoRange other) {
        Preconditions.requireNonNull(other, "other");

        return startNanoOfDay() <= other.startNanoOfDay() && other.endNanoOfDay() <= endNanoOfDay();
    }

    @Override
    default boolean containsBy(DailyNanoRange other) {
        Preconditions.requireNonNull(other, "other");

        return startNanoOfDay() >= other.startNanoOfDay() && other.endNanoOfDay() >= endNanoOfDay();
    }

    @Override
    default boolean overlaps(DailyNanoRange other) {
        Preconditions.requireNonNull(other, "other");

        return startNanoOfDay() < other.endNanoOfDay() && other.startNanoOfDay() < endNanoOfDay();
    }
}