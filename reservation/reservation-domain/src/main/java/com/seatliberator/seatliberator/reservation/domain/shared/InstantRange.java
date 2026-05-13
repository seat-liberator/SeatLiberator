package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Duration;
import java.time.Instant;

public interface InstantRange extends RangeComparable<InstantRange> {
    static void validate(Instant startAt, Instant endAt) {
        Preconditions.requireNonNull(startAt, "startAt");
        Preconditions.requireNonNull(endAt, "endAt");

        if (!startAt.isBefore(endAt)) throw new IllegalArgumentException("startAt must be before endAt.");
    }

    Instant startAt();

    Instant endAt();

    default Duration duration() {
        return Duration.between(startAt(), endAt());
    }

    default boolean isEnded(Instant time) {
        return !time.isBefore(endAt());
    }

    default boolean contains(Instant other) {
        Preconditions.requireNonNull(other, "other");

        return !other.isBefore(startAt()) && other.isBefore(endAt());
    }

    @Override
    default boolean isSame(InstantRange other) {
        Preconditions.requireNonNull(other, "other");

        return startAt().equals(other.startAt()) && endAt().equals(other.endAt());
    }

    @Override
    default boolean startsBefore(InstantRange other) {
        Preconditions.requireNonNull(other, "other");

        return startAt().isBefore(other.startAt());
    }

    @Override
    default boolean endsAfter(InstantRange other) {
        Preconditions.requireNonNull(other, "other");

        return endAt().isAfter(other.endAt());
    }

    @Override
    default boolean contains(InstantRange other) {
        Preconditions.requireNonNull(other, "other");

        return !startAt().isAfter(other.startAt()) && !endAt().isBefore(other.endAt());
    }

    @Override
    default boolean containsBy(InstantRange other) {
        Preconditions.requireNonNull(other, "other");

        return !startAt().isBefore(other.startAt()) && !endAt().isAfter(other.endAt());
    }

    @Override
    default boolean overlaps(InstantRange other) {
        Preconditions.requireNonNull(other, "other");

        return startAt().isBefore(other.endAt()) && other.startAt().isBefore(endAt());
    }
}
