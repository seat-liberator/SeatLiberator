package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Duration;
import java.time.LocalDate;
import java.util.stream.Stream;

public interface DateRange extends RangeComparable<DateRange> {
    static void validate(LocalDate startAt, LocalDate endAt) {
        Preconditions.requireNonNull(startAt, "startAt");
        Preconditions.requireNonNull(endAt, "endAt");

        if (!startAt.isBefore(endAt)) throw new IllegalArgumentException("startAt must be before endAt.");
    }

    LocalDate startAt();

    LocalDate endAt();

    default Duration duration() {
        return Duration.between(startAt(), endAt());
    }

    default Stream<LocalDate> stream() {
        return startAt().datesUntil(endAt().plusDays(1));
    }

    @Override
    default boolean isSame(DateRange other) {
        Preconditions.requireNonNull(other, "other");

        return startAt().equals(other.startAt()) && endAt().equals(other.endAt());
    }

    @Override
    default boolean startsBefore(DateRange other) {
        Preconditions.requireNonNull(other, "other");

        return startAt().isBefore(other.startAt());
    }

    @Override
    default boolean immediatelyBefore(DateRange other) {
        Preconditions.requireNonNull(other, "other");

        return endAt().equals(other.startAt());
    }

    @Override
    default boolean endsAfter(DateRange other) {
        Preconditions.requireNonNull(other, "other");

        return endAt().isAfter(other.endAt());
    }

    @Override
    default boolean immediatelyAfter(DateRange other) {
        Preconditions.requireNonNull(other, "other");

        return startAt().equals(other.endAt());
    }

    @Override
    default boolean contains(DateRange other) {
        Preconditions.requireNonNull(other, "other");

        return !startAt().isAfter(other.startAt()) && !endAt().isBefore(other.endAt());
    }

    @Override
    default boolean containsBy(DateRange other) {
        Preconditions.requireNonNull(other, "other");

        return !startAt().isBefore(other.startAt()) && !endAt().isAfter(other.endAt());
    }

    @Override
    default boolean overlaps(DateRange other) {
        Preconditions.requireNonNull(other, "other");

        return startAt().isBefore(other.endAt()) && other.startAt().isBefore(endAt());
    }
}
