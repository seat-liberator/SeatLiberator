package com.seatliberator.seatliberator.reservation.domain.shared;

import java.time.Duration;
import java.time.LocalTime;

public class DailyTimeSegmentFixtures {
    public static final LocalTime START_AT = LocalTime.of(6, 0);
    public static final Duration DURATION = Duration.ofHours(8);

    public static final DailyTimeSegment MORNING_SEGMENT = new DailyTimeSegmentFixtures.Builder()
            .startAt(LocalTime.of(8, 0))
            .duration(Duration.ofHours(4))
            .build();
    public static final DailyTimeSegment AFTERNOON_SEGMENT = new DailyTimeSegmentFixtures.Builder()
            .startAt(LocalTime.of(13, 0))
            .duration(Duration.ofHours(11))
            .build();
    public static final DailyTimeSegment DAWN_SEGMENT = new DailyTimeSegmentFixtures.Builder()
            .startAt(LocalTime.of(1, 0))
            .duration(Duration.ofHours(4))
            .build();

    public static DailyTimeSegment get() {
        return SimpleDailyTimeSegment.of(START_AT, DURATION);
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

        public DailyTimeSegment build() {
            return SimpleDailyTimeSegment.of(startAt, duration);
        }
    }
}
