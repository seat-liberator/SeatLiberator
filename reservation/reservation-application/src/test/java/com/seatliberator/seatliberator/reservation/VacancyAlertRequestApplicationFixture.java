package com.seatliberator.seatliberator.reservation;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestBehavior;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCreateCommand;

import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.fixture.ReservationFixture.INITIAL_USER_ID;
import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;

public class VacancyAlertRequestApplicationFixture {
    public static VacancyAlertRequestCreateCommand createVacancyRequestCreateCommand(
            String userId,
            SeatLocator locator,
            TimeRange range,
            VacancyAlertRequestBehavior behavior
    ) {
        return VacancyAlertRequestCreateCommand.from(userId, locator, range, behavior);
    }

    public static VacancyAlertRequestCreateCommand createVacancyRequestCreateCommand(
            SeatLocator locator,
            TimeRange range
    ) {
        return VacancyAlertRequestCreateCommand.from(
                INITIAL_USER_ID,
                locator,
                range,
                VacancyAlertRequestBehavior.AUTO_CLAIM
        );
    }

    public static VacancyAlertRequestCreateCommand createVacancyRequestCreateCommand() {
        return createVacancyRequestCreateCommand(
                INITIAL_USER_ID,
                createLocator(),
                createRange(),
                VacancyAlertRequestBehavior.AUTO_CLAIM
        );
    }

    public static VacancyAlertRequestCancelCommand createVacancyAlertRequestCancelCommand() {
        // TODO: kernel test support에 fixed / atomic increment 기반 UUID generator 따로 빼야함
        return createVacancyAlertRequestCancelCommand(INITIAL_USER_ID, UUID.randomUUID());
    }

    public static VacancyAlertRequestCancelCommand createVacancyAlertRequestCancelCommand(UUID waitlistId) {
        return createVacancyAlertRequestCancelCommand(INITIAL_USER_ID, waitlistId);
    }

    public static VacancyAlertRequestCancelCommand createVacancyAlertRequestCancelCommand(String userId, UUID waitlistId) {
        return new VacancyAlertRequestCancelCommand(userId, waitlistId);
    }
}
