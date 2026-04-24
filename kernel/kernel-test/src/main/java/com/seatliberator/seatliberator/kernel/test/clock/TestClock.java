package com.seatliberator.seatliberator.kernel.test.clock;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public class TestClock {
    private static final Instant REFERENCE_TIME = Instant.parse("2026-01-01T00:00:00Z");

    public static Clock getFixed() {
        return getFixed(REFERENCE_TIME, ZoneOffset.UTC);
    }

    public static Clock getFixed(Instant at) {
        return getFixed(at, ZoneOffset.UTC);
    }

    public static Clock getFixed(Instant at, ZoneOffset offset) {
        return Clock.fixed(at, offset);
    }
}
