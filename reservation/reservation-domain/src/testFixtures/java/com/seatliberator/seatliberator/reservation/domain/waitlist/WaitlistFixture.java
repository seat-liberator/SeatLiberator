package com.seatliberator.seatliberator.reservation.domain.waitlist;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;

public class WaitlistFixture {
    public static final String INITIAL_USER_ID = "user-1";

    public static Waitlist createWaitlist() {
        return createWaitlist(fixedClock.instant().minusSeconds(1));
    }

    public static Waitlist createWaitlist(Instant requestedAt) {
        return Waitlist.autoClaim(
                INITIAL_USER_ID,
                createLocator(),
                createRange(),
                requestedAt
        );
    }
}
