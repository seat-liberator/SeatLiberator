package com.seatliberator.seatliberator.reservation;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.WaitlistBehavior;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.command.CancelWaitlistCommand;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.command.CreateWaitlistCommand;

import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.fixture.ReservationFixture.INITIAL_USER_ID;
import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;

public class VacancyAlertRequestApplicationFixture {
    public static CreateWaitlistCommand createVacancyRequestCreateCommand(
            String userId,
            SeatLocator locator,
            TimeRange range,
            WaitlistBehavior behavior
    ) {
        return CreateWaitlistCommand.from(userId, locator, range, behavior);
    }

    public static CreateWaitlistCommand createVacancyRequestCreateCommand(
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

    public static CreateWaitlistCommand createVacancyRequestCreateCommand() {
        return createVacancyRequestCreateCommand(
                INITIAL_USER_ID,
                createLocator(),
                createRange(),
                WaitlistBehavior.AUTO_CLAIM
        );
    }

    public static CancelWaitlistCommand createVacancyAlertRequestCancelCommand() {
        // TODO: kernel test support에 fixed / atomic increment 기반 UUID generator 따로 빼야함
        return createVacancyAlertRequestCancelCommand(INITIAL_USER_ID, UUID.randomUUID());
    }

    public static CancelWaitlistCommand createVacancyAlertRequestCancelCommand(UUID waitlistId) {
        return createVacancyAlertRequestCancelCommand(INITIAL_USER_ID, waitlistId);
    }

    public static CancelWaitlistCommand createVacancyAlertRequestCancelCommand(String userId, UUID waitlistId) {
        return new CancelWaitlistCommand(userId, waitlistId);
    }
}
