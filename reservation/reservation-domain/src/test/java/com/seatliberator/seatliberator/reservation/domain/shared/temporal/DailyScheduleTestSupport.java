package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public class DailyScheduleTestSupport {
    public static ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    public static ZoneId UTC = ZoneId.of("UTC");
    public static ZoneId NEW_YORK = ZoneId.of("America/New_York");

    public static long nanoOf(String value) {
        return LocalTime.parse(value).toNanoOfDay();
    }

    public static SimpleDailyNanoRange range(String startAt, String endAt) {
        return SimpleDailyNanoRange.of(nanoOf(startAt), nanoOf(endAt));
    }

    public static SimpleDailyNanoRange range(String startAt, long endNanoOfDay) {
        return SimpleDailyNanoRange.of(nanoOf(startAt), endNanoOfDay);
    }

    public static InstantRange withinDayInstantRange() {
        return SimpleInstantRange.of(
                Instant.parse("2026-01-01T01:00:00Z"),
                Instant.parse("2026-01-01T08:00:00Z")
        );
    }

    public static InstantRange crossingMidnightInstantRange() {
        return SimpleInstantRange.of(
                Instant.parse("2026-01-01T13:30:00Z"),
                Instant.parse("2026-01-01T16:30:00Z")
        );
    }

    public static InstantRange longerThanDayInstantRange() {
        return SimpleInstantRange.of(
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-03T06:00:00Z")
        );
    }

    public static InstantRange longChunkCheckedInstantRange() {
        return SimpleInstantRange.of(
                Instant.parse("2026-01-01T10:00:00Z"),
                Instant.parse("2026-01-02T02:00:00Z")
        );
    }

    public static InstantRange endingAtMidnightInstantRange() {
        return SimpleInstantRange.of(
                Instant.parse("2026-01-01T13:00:00Z"),
                Instant.parse("2026-01-01T15:00:00Z")
        );
    }

    public static InstantRange multiDayChunkInstantRange() {
        return SimpleInstantRange.of(
                Instant.parse("2026-01-01T23:00:00Z"),
                Instant.parse("2026-01-03T01:00:00Z")
        );
    }

    public static InstantRange dstTransitionInstantRange() {
        return SimpleInstantRange.of(
                Instant.parse("2026-03-08T05:00:00Z"),
                Instant.parse("2026-03-09T04:00:00Z")
        );
    }
}
