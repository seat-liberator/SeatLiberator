package com.seatliberator.seatliberator.reservation.domain.shared;

import java.time.Instant;

public interface InstantRange {
    Instant startAt();

    Instant endAt();

    default void validate(Instant startAt, Instant endAt) {
        if (!startAt.isBefore(endAt)) throw new IllegalArgumentException("startAt must be before endAt.");
    }

    default boolean contains(Instant time) {
        return !time.isBefore(startAt()) && time.isBefore(endAt());
    }

    default boolean isEnded(Instant time) {
        return !time.isBefore(endAt());
    }

    default boolean isSame(InstantRange other) {
        return startAt().equals(other.startAt()) && endAt().equals(other.endAt());
    }
}
