package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public interface DailyTimeSegments {
    List<? extends DailyTimeSegment> segments();

    default boolean contains(Instant at, ZoneId zoneId) {
        Preconditions.requireNonNull(at, "at");
        Preconditions.requireNonNull(zoneId, "zoneId");

        return segments().stream()
                .anyMatch(segment -> segment.contains(at, zoneId));
    }

    default boolean contains(LocalTime at) {
        Preconditions.requireNonNull(at, "at");

        return segments().stream()
                .anyMatch(segment -> segment.contains(at));
    }

    default boolean overlaps(DailyTimeSegment other) {
        Preconditions.requireNonNull(other, "other");

        return segments().stream()
                .anyMatch(segment -> segment.overlaps(other));
    }

    default boolean overlaps(DailyTimeSegments other) {
        Preconditions.requireNonNull(other, "other");

        return segments().stream()
                .anyMatch(left -> other.segments().stream()
                        .anyMatch(left::overlaps));
    }

    default void validate(List<? extends DailyTimeSegment> segments) {
        Preconditions.requireNonNull(segments, "segments");

        if (segments.isEmpty())
            throw new IllegalArgumentException("segments must not be empty.");

        for (var segment : segments) {
            Preconditions.requireNonNull(segment, "segment");
        }

        for (int i = 0; i < segments.size(); i++) {
            for (int j = i + 1; j < segments.size(); j++) {
                if (segments.get(i).overlaps(segments.get(j))) {
                    throw new IllegalArgumentException("segments must not overlap.");
                }
            }
        }
    }
}
