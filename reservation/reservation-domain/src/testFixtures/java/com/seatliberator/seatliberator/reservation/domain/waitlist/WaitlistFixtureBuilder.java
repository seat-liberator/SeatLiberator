package com.seatliberator.seatliberator.reservation.domain.waitlist;

import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocatorFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRangeFixture;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistFixture.INITIAL_USER_ID;

public class WaitlistFixtureBuilder {
    private String userId = INITIAL_USER_ID;

    private SeatLocator locator = SeatLocatorFixture.get();

    private InstantRange range = InstantRangeFixture.get();

    private Instant requestedAt = fixedClock.instant();

    private WaitlistBehavior behavior = WaitlistBehavior.NOTIFY_ONLY;

    public WaitlistFixtureBuilder() {
    }

    public WaitlistFixtureBuilder(
            String userId,
            SeatLocator locator,
            InstantRange range,
            Instant requestedAt,
            WaitlistBehavior behavior
    ) {
        this.userId = userId;
        this.locator = locator;
        this.range = range;
        this.requestedAt = requestedAt;
        this.behavior = behavior;
    }

    public WaitlistFixtureBuilder copy() {
        return new WaitlistFixtureBuilder(userId, locator, range, requestedAt, behavior);
    }

    public WaitlistFixtureBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public WaitlistFixtureBuilder locator(SeatLocator locator) {
        this.locator = locator;
        return this;
    }

    public WaitlistFixtureBuilder range(InstantRange range) {
        this.range = range;
        return this;
    }

    public WaitlistFixtureBuilder requestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
        return this;
    }

    public WaitlistFixtureBuilder behavior(WaitlistBehavior behavior) {
        this.behavior = behavior;
        return this;
    }

    public Waitlist build() {
        return Waitlist.create(userId, locator, range, behavior, requestedAt);
    }
}
