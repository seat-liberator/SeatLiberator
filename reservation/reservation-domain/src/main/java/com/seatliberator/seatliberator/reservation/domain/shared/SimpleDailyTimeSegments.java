package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.List;

public record SimpleDailyTimeSegments(
        List<DailyTimeSegment> segments
) implements DailyTimeSegments {
    public SimpleDailyTimeSegments {
        segments = DailyTimeSegments.validateAndSort(segments);
    }

    public static SimpleDailyTimeSegments of(List<DailyTimeSegment> segments) {
        return new SimpleDailyTimeSegments(segments);
    }

    public static SimpleDailyTimeSegments from(DailyTimeSegments segments) {
        Preconditions.requireNonNull(segments, "segments");

        return of(segments.segments());
    }
}
