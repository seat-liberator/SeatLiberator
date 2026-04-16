package com.seatliberator.seatliberator.reservation.domain.fixture;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.WaitlistBehavior;
import com.seatliberator.seatliberator.reservation.domain.persistence.Waitlist;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static com.seatliberator.seatliberator.reservation.domain.fixture.VacancyAlertRequestFixture.INITIAL_USER_ID;

public class VacancyAlertRequestFixtureBuilder {
    private String userId = INITIAL_USER_ID;

    private SeatLocator locator = createLocator();

    private TimeRange range = createRange();

    private Instant requestedAt = fixedClock.instant();

    private WaitlistBehavior behavior = WaitlistBehavior.NOTIFY_ONLY;

    public VacancyAlertRequestFixtureBuilder() {
    }

    public VacancyAlertRequestFixtureBuilder(
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

    public VacancyAlertRequestFixtureBuilder copy() {
        return new VacancyAlertRequestFixtureBuilder(userId, locator, range, requestedAt, behavior);
    }

    public VacancyAlertRequestFixtureBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public VacancyAlertRequestFixtureBuilder locator(SeatLocator locator) {
        this.locator = locator;
        return this;
    }

    public VacancyAlertRequestFixtureBuilder range(TimeRange range) {
        this.range = range;
        return this;
    }

    public VacancyAlertRequestFixtureBuilder requestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
        return this;
    }

    public VacancyAlertRequestFixtureBuilder behavior(WaitlistBehavior actionType) {
        this.behavior = actionType;
        return this;
    }

    public Waitlist build() {
        return Waitlist.create(userId, locator, range, behavior, requestedAt);
    }
}
