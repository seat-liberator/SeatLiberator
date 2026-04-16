package com.seatliberator.seatliberator.reservation.domain.fixture;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.WaitlistBehavior;
import com.seatliberator.seatliberator.reservation.domain.persistence.Waitlist;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static com.seatliberator.seatliberator.reservation.domain.fixture.WaitlistFixture.INITIAL_USER_ID;

public class WaitlistFixtureBuilder {
    private String userId = INITIAL_USER_ID;

    private SeatLocator locator = createLocator();

    private TimeRange range = createRange();

    private Instant requestedAt = fixedClock.instant();

    private WaitlistBehavior behavior = WaitlistBehavior.NOTIFY_ONLY;

    public WaitlistFixtureBuilder() {
    }

    public WaitlistFixtureBuilder(
            String userId,
            SeatLocator locator,
            TimeRange range,
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

    public WaitlistFixtureBuilder range(TimeRange range) {
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
