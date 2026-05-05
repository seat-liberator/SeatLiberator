package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.LocalTime;

public record SimpleDailyTimeWindow(
        LocalTime startAt,
        LocalTime endAt
) implements DailyTimeWindow {
    public SimpleDailyTimeWindow {
        Preconditions.requireNonNull(startAt, "startAt");
        Preconditions.requireNonNull(endAt, "endAt");
        validate(startAt, endAt);
    }

    public static SimpleDailyTimeWindow of(LocalTime startAt, LocalTime endAt) {
        return new SimpleDailyTimeWindow(startAt, endAt);
    }

    public static SimpleDailyTimeWindow from(DailyTimeWindow dailyTimeWindow) {
        Preconditions.requireNonNull(dailyTimeWindow, "dailyTimeWindow");
        return of(dailyTimeWindow.startAt(), dailyTimeWindow.endAt());
    }
}
