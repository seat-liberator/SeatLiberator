package com.seatliberator.seatliberator.reservation.domain;

import java.time.Instant;

public interface TimeRange {
    Instant startAt();

    Instant endAt();

    default boolean contains(Instant time) {
        return !time.isBefore(startAt()) && time.isBefore(endAt());
    }

    default boolean isEnded(Instant time) {
        return !time.isBefore(endAt());
    }
}
