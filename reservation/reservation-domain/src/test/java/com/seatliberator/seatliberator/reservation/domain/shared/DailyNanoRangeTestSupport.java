package com.seatliberator.seatliberator.reservation.domain.shared;

import java.time.Duration;
import java.time.LocalTime;

public class DailyNanoRangeTestSupport {
    public static LocalTime START_AT = LocalTime.of(12, 0);
    public static Duration DURATION = Duration.ofHours(10);

    public static long START_NANO_OF_DAY = START_AT.toNanoOfDay();
    public static long END_NANO_OF_DAY = START_NANO_OF_DAY + DURATION.toNanos();
    public static long BEFORE_END_NANO_OF_DAY = END_NANO_OF_DAY - Duration.ofHours(1).toNanos();
    public static long AFTER_END_NANO_OF_DAY = END_NANO_OF_DAY + Duration.ofHours(1).toNanos();
    public static long BEFORE_START_NANO_OF_DAY = START_NANO_OF_DAY - Duration.ofHours(1).toNanos();
    public static long AFTER_START_NANO_OF_DAY = START_NANO_OF_DAY + Duration.ofHours(1).toNanos();

}
