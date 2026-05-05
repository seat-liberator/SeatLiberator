package com.seatliberator.seatliberator.reservation.domain.shared;

import java.time.LocalTime;

public class DailyTimeWindowFixture {
    public static final LocalTime START_AT = LocalTime.of(6, 0);
    public static final LocalTime END_AT = LocalTime.of(0, 0);

    public static DailyTimeWindow get() {
        return SimpleDailyTimeWindow.of(START_AT, END_AT);
    }

    public static class Builder {
        private LocalTime startAt = START_AT;
        private LocalTime endAt = END_AT;

        public Builder openAt(LocalTime openAt) {
            this.startAt = openAt;
            return this;
        }

        public Builder closeAt(LocalTime closeAt) {
            this.endAt = closeAt;
            return this;
        }

        public DailyTimeWindow build() {
            return SimpleDailyTimeWindow.of(startAt, endAt);
        }
    }
}
