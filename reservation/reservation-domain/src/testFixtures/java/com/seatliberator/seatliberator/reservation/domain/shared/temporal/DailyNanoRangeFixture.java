package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import java.time.Duration;
import java.time.LocalTime;

public class DailyNanoRangeFixture {
    public static final LocalTime START_AT = LocalTime.of(6, 0);
    public static final Duration DURATION = Duration.ofHours(8);

    public static final DailyNanoRange MORNING_RANGE = new DailyNanoRangeFixture.Builder()
            .startAt(LocalTime.of(8, 0))
            .duration(Duration.ofHours(4))
            .build();
    public static final DailyNanoRange AFTERNOON_RANGE = new DailyNanoRangeFixture.Builder()
            .startAt(LocalTime.of(13, 0))
            .duration(Duration.ofHours(11))
            .build();
    public static final DailyNanoRange DAWN_RANGE = new DailyNanoRangeFixture.Builder()
            .startAt(LocalTime.of(1, 0))
            .duration(Duration.ofHours(4))
            .build();
    public static final DailyNanoRange MORNING_SLOT_RANGE = new DailyNanoRangeFixture.Builder()
            .startAt(LocalTime.of(8, 0))
            .duration(Duration.ofHours(2))
            .build();
    public static final DailyNanoRange AFTERNOON_SLOT_RANGE = new DailyNanoRangeFixture.Builder()
            .startAt(LocalTime.of(13, 0))
            .duration(Duration.ofHours(2))
            .build();
    public static final DailyNanoRange NIGHT_SLOT_RANGE = new DailyNanoRangeFixture.Builder()
            .startAt(LocalTime.of(19, 0))
            .duration(Duration.ofHours(2))
            .build();

    public static DailyNanoRange get() {
        return SimpleDailyNanoRange.of(START_AT, DURATION);
    }

    public static class Builder {
        private LocalTime startAt = START_AT;
        private Duration duration = DURATION;

        public Builder startAt(LocalTime startAt) {
            this.startAt = startAt;
            return this;
        }

        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public DailyNanoRange build() {
            return SimpleDailyNanoRange.of(startAt, duration);
        }
    }
}
