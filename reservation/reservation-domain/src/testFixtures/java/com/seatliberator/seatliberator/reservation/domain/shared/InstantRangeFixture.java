package com.seatliberator.seatliberator.reservation.domain.shared;

import java.time.Duration;
import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;

public class InstantRangeFixture {
    public static final Duration INITIAL_DURATION = Duration.ofMinutes(30);
    public static final Instant INITIAL_START_AT = fixedClock.instant().plusSeconds(60);
    public static final Instant INITIAL_END_AT = INITIAL_START_AT.plus(INITIAL_DURATION);

    public static InstantRange get() {
        return get(INITIAL_START_AT, INITIAL_END_AT);
    }

    public static InstantRange get(Instant startAt) {
        var endAt = startAt.plus(INITIAL_DURATION);
        return get(startAt, endAt);
    }

    public static InstantRange get(String startAt, String endAt) {
        return SimpleInstantRange.of(Instant.parse(startAt), Instant.parse(endAt));
    }

    public static InstantRange get(Instant startAt, Instant endAt) {
        return SimpleInstantRange.of(startAt, endAt);
    }
}
