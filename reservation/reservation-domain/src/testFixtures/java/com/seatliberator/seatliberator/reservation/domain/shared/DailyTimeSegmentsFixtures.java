package com.seatliberator.seatliberator.reservation.domain.shared;

import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegmentFixtures.*;

public class DailyTimeSegmentsFixtures {

    public static DailyTimeSegments get() {
        return SimpleDailyTimeSegments.of(List.of(
                MORNING_SEGMENT,
                AFTERNOON_SEGMENT,
                DAWN_SEGMENT
        ));
    }

    public static class Builder {
        private List<DailyTimeSegment> segments = List.of(MORNING_SEGMENT, AFTERNOON_SEGMENT, DAWN_SEGMENT);

        public Builder segments(List<DailyTimeSegment> segments) {
            this.segments = segments;
            return this;
        }

        public DailyTimeSegments build() {
            return SimpleDailyTimeSegments.of(segments);
        }
    }
}
