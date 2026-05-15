package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public interface DailySchedule {
    static List<DailyNanoRange> validateAndSort(Collection<? extends DailyNanoRange> ranges) {
        Preconditions.requireNonNull(ranges, "ranges");

        if (ranges.isEmpty())
            throw new IllegalArgumentException("ranges must not be empty.");

        var sorted = ranges.stream()
                .<DailyNanoRange>map(range -> Preconditions.requireNonNull(range, "range"))
                .<DailyNanoRange>map(SimpleDailyNanoRange::from)
                .sorted(Comparator
                        .comparing(DailyNanoRange::startNanoOfDay)
                        .thenComparing(DailyNanoRange::endNanoOfDay)
                )
                .toList();

        for (int i = 1; i < sorted.size(); i++) {
            var previous = sorted.get(i - 1);
            var current = sorted.get(i);

            if (current.overlaps(previous)) {
                throw new IllegalArgumentException("ranges must not overlap. previous=" + previous + ", current=" + current);
            }
        }

        return sorted;
    }

    List<DailyNanoRange> ranges();

    default boolean contains(long other) {
        return ranges().stream().anyMatch(range -> range.contains(other));
    }

    default boolean contains(LocalTime other) {
        Preconditions.requireNonNull(other, "other");

        return contains(other.toNanoOfDay());
    }

    default boolean contains(Instant other, ZoneId zoneId) {
        Preconditions.requireNonNull(other, "other");
        Preconditions.requireNonNull(zoneId, "zoneId");

        return ranges().stream().anyMatch(range -> range.contains(other, zoneId));
    }

    default boolean contains(DailyNanoRange other) {
        Preconditions.requireNonNull(other, "other");

        var cursor = other.startNanoOfDay();

        for (var range : ranges()) {
            if (range.endNanoOfDay() <= cursor) continue;
            if (range.startNanoOfDay() > cursor) return false;

            cursor = Math.max(cursor, range.endNanoOfDay());

            if (cursor >= other.endNanoOfDay()) return true;
        }

        return false;
    }

    default boolean isAlways() {
        return contains(SimpleDailyNanoRange.of(0, DailyNanoRange.DAY_NANOS));
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
                    ? DailyNanoRange.DAY_NANOS
                    : chunkEnd.toLocalTime().toNanoOfDay();

            if (!contains(SimpleDailyNanoRange.of(startNano, endNano))) {
                return false;
            }

            cursor = chunkEnd;
        }

        return true;
    }

    default boolean overlaps(DailyNanoRange other) {
        Preconditions.requireNonNull(other, "other");

        return ranges().stream()
                .anyMatch(range -> range.overlaps(other));
    }

    default boolean overlaps(DailySchedule other) {
        Preconditions.requireNonNull(other, "other");

        return ranges().stream()
                .anyMatch(left -> other.ranges().stream()
                        .anyMatch(left::overlaps));
    }
}
