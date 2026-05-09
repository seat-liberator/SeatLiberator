package com.seatliberator.seatliberator.reservation.domain.shared;

import java.util.Comparator;
import java.util.List;

public record SimpleDailyTimeSegments(
        List<SimpleDailyTimeSegment> segments
) implements DailyTimeSegments {
    public SimpleDailyTimeSegments {
        validate(segments);

        segments = segments.stream()
                .sorted(Comparator.comparingLong(DailyTimeSegment::startNanoOfDay))
                .toList();
    }

    public static SimpleDailyTimeSegments of(List<? extends DailyTimeSegment> segments) {
        return new SimpleDailyTimeSegments(
                segments.stream()
                        .map(SimpleDailyTimeSegment::from)
                        .toList()
        );
    }
}
