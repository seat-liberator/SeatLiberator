package com.seatliberator.seatliberator.reservation;

import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCommand;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.ReservationFixture.*;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;

public class ReservationApplicationFixture {

    public static VacancyAlertRequestCommand createVacancyAlertRequestCommand(Instant startTime, Instant endTime) {
        return new VacancyAlertRequestCommand(INITIAL_USER_ID, INITIAL_ROOM_ID, INITIAL_SEAT_ID, startTime, endTime);
    }

    public static VacancyAlertRequestCommand createVacancyAlertRequestCommand(Instant requestedAt) {
        var startTime = fixedClock.instant();
        var endTime = startTime.plus(INITIAL_DURATION);
        return createVacancyAlertRequestCommand(startTime, endTime);
    }
}
