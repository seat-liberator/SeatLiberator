package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRangeFixture.*;

public class DailyScheduleFixture {

    public static DailySchedule get() {
        return SimpleDailySchedule.of(List.of(
                MORNING_RANGE,
                AFTERNOON_RANGE,
                DAWN_RANGE
        ));
    }

    public static class Builder {
        private List<DailyNanoRange> ranges = List.of(MORNING_RANGE, AFTERNOON_RANGE, DAWN_RANGE);

        public Builder ranges(List<DailyNanoRange> ranges) {
            this.ranges = ranges;
            return this;
        }

        public DailySchedule build() {
            return SimpleDailySchedule.of(ranges);
        }
    }
}
