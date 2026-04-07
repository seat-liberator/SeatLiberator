package com.seatliberator.seatliberator.reservation.domain.fixture;

import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;

public class VacancyAlertRequestFixture {
    public static final String INITIAL_USER_ID = "user-1";

    public static VacancyAlertRequest createAlert() {
        return createAlert(fixedClock.instant().minusSeconds(1));
    }

    public static VacancyAlertRequest createAlert(Instant requestedAt) {
        return VacancyAlertRequest.create(
                INITIAL_USER_ID,
                createLocator(),
                createRange(),
                requestedAt
        );
    }
}
