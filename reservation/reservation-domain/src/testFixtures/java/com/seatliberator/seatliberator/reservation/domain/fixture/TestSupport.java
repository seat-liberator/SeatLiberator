package com.seatliberator.seatliberator.reservation.domain.fixture;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

public class TestSupport {
    public static final Clock fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

    public static final UUID INITIAL_UUID = new UUID(0L, 1L);
}
