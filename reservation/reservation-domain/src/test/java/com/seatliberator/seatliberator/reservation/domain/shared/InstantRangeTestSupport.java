package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class InstantRangeTestSupport {
    private static final Clock clock = TestClock.getFixed();

    public static Instant START_AT = clock.instant();
    public static Instant END_AT = START_AT.plusSeconds(10);
    public static Duration DURATION = Duration.between(START_AT, END_AT);
    public static Instant BEFORE_END_AT = END_AT.minusSeconds(3);
    public static Instant AFTER_END_AT = END_AT.plusSeconds(3);
    public static Instant BEFORE_START_AT = START_AT.minusSeconds(3);
    public static Instant AFTER_START_AT = START_AT.plusSeconds(3);
}
