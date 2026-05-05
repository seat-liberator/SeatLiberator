package com.seatliberator.seatliberator.reservation.domain.shared;

import java.time.LocalTime;

public interface DailyTimeWindow {
    LocalTime startAt();

    LocalTime endAt();

    default void validate(LocalTime startAt, LocalTime endAt) {
        if (startAt.equals(endAt)) throw new IllegalArgumentException("startAt and endAt must not be same.");
    }

    default boolean isSame(DailyTimeWindow other) {
        return startAt().equals(other.startAt()) && endAt().equals(other.endAt());
    }
}