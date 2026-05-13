package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public interface DailyTimeSegments {
    static List<DailyTimeSegment> validateAndSort(Collection<? extends DailyTimeSegment> segments) {
        Preconditions.requireNonNull(segments, "segments");

        if (segments.isEmpty())
            throw new IllegalArgumentException("segments must not be empty.");

        var sorted = segments.stream()
                .<DailyTimeSegment>map(segment -> Preconditions.requireNonNull(segment, "segment"))
                .<DailyTimeSegment>map(SimpleDailyTimeSegment::from)
                .sorted(Comparator
                        .comparing(DailyTimeSegment::startNanoOfDay)
                        .thenComparing(DailyTimeSegment::endNanoOfDay)
                )
                .toList();

        for (int i = 1; i < sorted.size(); i++) {
            var previous = sorted.get(i - 1);
            var current = sorted.get(i);

            if (current.overlaps(previous)) {
                throw new IllegalArgumentException("segments must not overlap. previous=" + previous + ", current=" + current);
            }
        }

        return sorted;
    }

    List<DailyTimeSegment> segments();

    default boolean contains(long other) {
        return segments().stream().anyMatch(range -> range.contains(other));
    }

    default boolean contains(LocalTime other) {
        Preconditions.requireNonNull(other, "other");

        return contains(other.toNanoOfDay());
    }

    default boolean contains(Instant other, ZoneId zoneId) {
        Preconditions.requireNonNull(other, "other");
        Preconditions.requireNonNull(zoneId, "zoneId");

        return segments().stream().anyMatch(range -> range.contains(other, zoneId));
    }

    default boolean contains(DailyTimeSegment other) {
        Preconditions.requireNonNull(other, "other");

        var cursor = other.startNanoOfDay();

        for (var segment : segments()) {
            if (segment.endNanoOfDay() <= cursor) continue;
            if (segment.startNanoOfDay() > cursor) return false;

            cursor = Math.max(cursor, segment.endNanoOfDay());

            if (cursor >= other.endNanoOfDay()) return true;
        }

        return false;
    }

    default boolean isAlways() {
        return contains(SimpleDailyTimeSegment.of(0, DailyTimeSegment.DAY_NANOS));
    }

    default boolean contains(InstantRange other, ZoneId zoneId) {
        Preconditions.requireNonNull(other, "other");
        Preconditions.requireNonNull(zoneId, "zoneId");

        var cursor = other.startAt().atZone(zoneId);
        var endZdt = other.endAt().atZone(zoneId);

        while (cursor.isBefore(endZdt)) {
            var nextMidnight = cursor.toLocalDate().plusDays(1).atStartOfDay(zoneId);
            var chunkEnd = nextMidnight.isBefore(endZdt)
                    ? nextMidnight
                    : endZdt;

            var startNano = cursor.toLocalTime().toNanoOfDay();
            var endNano = chunkEnd.toLocalTime().equals(LocalTime.MIDNIGHT)
                    ? DailyTimeSegment.DAY_NANOS
                    : chunkEnd.toLocalTime().toNanoOfDay();

            if (!contains(SimpleDailyTimeSegment.of(startNano, endNano))) {
                return false;
            }

            cursor = chunkEnd;
        }

        return true;
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
}
