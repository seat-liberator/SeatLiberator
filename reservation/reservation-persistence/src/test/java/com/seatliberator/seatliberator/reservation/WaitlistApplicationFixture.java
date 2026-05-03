package com.seatliberator.seatliberator.reservation;

import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command.CancelWaitlistCommand;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command.CreateWaitlistCommand;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistBehavior;

import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture.INITIAL_USER_ID;

public class WaitlistApplicationFixture {
    public static CreateWaitlistCommand createWaitlistCreateCommand(
            String userId,
            SeatLocator locator,
            TimeRange range,
            WaitlistBehavior behavior
    ) {
        return CreateWaitlistCommand.from(userId, locator, range, behavior);
    }

    public static CreateWaitlistCommand createWaitlistCreateCommand(
            SeatLocator locator,
            TimeRange range
    ) {
        return CreateWaitlistCommand.from(
                INITIAL_USER_ID,
                locator,
                range,
                WaitlistBehavior.AUTO_CLAIM
        );
    }

    public static CreateWaitlistCommand createWaitlistCreateCommand() {
        return createWaitlistCreateCommand(
                INITIAL_USER_ID,
                createLocator(),
                createRange(),
                WaitlistBehavior.AUTO_CLAIM
        );
    }

    public static CancelWaitlistCommand createWaitlistCancelCommand() {
        return createWaitlistCancelCommand(INITIAL_USER_ID, UUID.randomUUID());
    }

    public static CancelWaitlistCommand createWaitlistCancelCommand(UUID waitlistId) {
        return createWaitlistCancelCommand(INITIAL_USER_ID, waitlistId);
    }

    public static CancelWaitlistCommand createWaitlistCancelCommand(String userId, UUID waitlistId) {
        return new CancelWaitlistCommand(userId, waitlistId);
    }
}
