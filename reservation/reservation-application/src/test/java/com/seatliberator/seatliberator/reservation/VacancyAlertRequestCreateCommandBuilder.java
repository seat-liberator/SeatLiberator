package com.seatliberator.seatliberator.reservation;

import com.seatliberator.seatliberator.reservation.domain.*;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.command.CreateWaitlistCommand;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static com.seatliberator.seatliberator.reservation.domain.fixture.VacancyAlertRequestFixture.INITIAL_USER_ID;

public class VacancyAlertRequestCreateCommandBuilder {
    private String userId = INITIAL_USER_ID;
    private SeatLocator locator = createLocator();
    private TimeRange range = createRange();
    private WaitlistBehavior behavior = WaitlistBehavior.AUTO_CLAIM;

    public VacancyAlertRequestCreateCommandBuilder() {}

    public VacancyAlertRequestCreateCommandBuilder(
            String userId,
            SeatLocator locator,
            TimeRange range,
            WaitlistBehavior behavior
    ) {
        this.userId = userId;
        this.locator = locator;
        this.range = range;
        this.behavior = behavior;
    }

    public VacancyAlertRequestCreateCommandBuilder copy() {
        return new VacancyAlertRequestCreateCommandBuilder(
                userId,
                locator,
                range,
                behavior
        );
    }

    public CreateWaitlistCommand build() {
        return CreateWaitlistCommand.from(
                userId,
                locator,
                range,
                behavior
        );
    }

    public VacancyAlertRequestCreateCommandBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public VacancyAlertRequestCreateCommandBuilder locator(SeatLocator locator) {
        this.locator = locator;
        return this;
    }

    public VacancyAlertRequestCreateCommandBuilder locator(String roomId, String seatId) {
        this.locator = SimpleSeatLocator.from(roomId, seatId);
        return this;
    }

    public VacancyAlertRequestCreateCommandBuilder range(TimeRange range) {
        this.range = range;
        return this;
    }

    public VacancyAlertRequestCreateCommandBuilder range(Instant startAt, Instant endAt) {
        this.range = SimpleTimeRange.from(startAt, endAt);
        return this;
    }

    public VacancyAlertRequestCreateCommandBuilder behavior(WaitlistBehavior behavior) {
        this.behavior = behavior;
        return this;
    }
}
