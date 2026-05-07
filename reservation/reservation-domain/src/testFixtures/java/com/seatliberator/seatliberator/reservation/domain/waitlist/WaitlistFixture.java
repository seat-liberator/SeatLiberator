package com.seatliberator.seatliberator.reservation.domain.waitlist;

import com.seatliberator.seatliberator.reservation.domain.shared.InstantRangeFixture;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.shared.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;

public class WaitlistFixture {
    public static final String INITIAL_USER_ID = "user-1";

    public static Waitlist createWaitlist() {
        return createWaitlist(fixedClock.instant().minusSeconds(1));
    }

    public static Waitlist createWaitlist(Instant requestedAt) {
        return Waitlist.autoClaim(
                INITIAL_USER_ID,
                createLocator(),
                InstantRangeFixture.get(),
                requestedAt
        );
    }
}
