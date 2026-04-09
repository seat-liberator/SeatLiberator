package com.seatliberator.seatliberator.reservation.domain.fixture;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestBehavior;
import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;

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

    private VacancyAlertRequestBehavior actionType = VacancyAlertRequestBehavior.NOTIFY_ONLY;

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

    public VacancyAlertRequestFixtureBuilder actionType(VacancyAlertRequestBehavior actionType) {
        this.actionType = actionType;
        return this;
    }

    public VacancyAlertRequestFixtureBuilder notifyOnly() {
        this.actionType = VacancyAlertRequestBehavior.NOTIFY_ONLY;
        return this;
    }

    public VacancyAlertRequestFixtureBuilder queue() {
        this.actionType = VacancyAlertRequestBehavior.AUTO_CLAIM;
        return this;
    }

    public VacancyAlertRequest build() {
        return switch(actionType) {
            case AUTO_CLAIM -> VacancyAlertRequest.autoClaim(userId, locator, range, requestedAt);
            case NOTIFY_ONLY -> VacancyAlertRequest.notifyOnly(userId, locator, range, requestedAt);
        };
    }


}
