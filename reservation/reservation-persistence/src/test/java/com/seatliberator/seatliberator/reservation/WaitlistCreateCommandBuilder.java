package com.seatliberator.seatliberator.reservation;

import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command.CreateWaitlistCommand;
import com.seatliberator.seatliberator.reservation.domain.*;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static com.seatliberator.seatliberator.reservation.domain.fixture.WaitlistFixture.INITIAL_USER_ID;

public class WaitlistCreateCommandBuilder {
    private String userId = INITIAL_USER_ID;
    private SeatLocator locator = createLocator();
    private TimeRange range = createRange();
    private WaitlistBehavior behavior = WaitlistBehavior.AUTO_CLAIM;

    public WaitlistCreateCommandBuilder() {
    }

    public WaitlistCreateCommandBuilder(
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

    public WaitlistCreateCommandBuilder copy() {
        return new WaitlistCreateCommandBuilder(
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

    public WaitlistCreateCommandBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public WaitlistCreateCommandBuilder locator(SeatLocator locator) {
        this.locator = locator;
        return this;
    }

    public WaitlistCreateCommandBuilder locator(String roomId, String seatId) {
        this.locator = SimpleSeatLocator.of(roomId, seatId);
        return this;
    }

    public WaitlistCreateCommandBuilder range(TimeRange range) {
        this.range = range;
        return this;
    }

    public WaitlistCreateCommandBuilder range(Instant startAt, Instant endAt) {
        this.range = SimpleTimeRange.of(startAt, endAt);
        return this;
    }

    public WaitlistCreateCommandBuilder behavior(WaitlistBehavior behavior) {
        this.behavior = behavior;
        return this;
    }
}
