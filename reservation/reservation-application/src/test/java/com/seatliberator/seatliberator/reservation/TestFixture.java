package com.seatliberator.seatliberator.reservation;

import com.seatliberator.seatliberator.reservation.book.domain.Reservation;
import com.seatliberator.seatliberator.reservation.book.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCommand;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Fail.fail;

public class TestFixture {
    public static final String INITIAL_USER_ID = "user-1";
    public static final String INITIAL_ROOM_ID = "room-1";
    public static final String INITIAL_SEAT_ID = "seat-1";
    public static final Clock fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
    public static final Duration INITIAL_DURATION = Duration.ofMinutes(30);

    public static Reservation createReservation() {
        var startTime = fixedClock.instant();
        var endTime = startTime.plus(INITIAL_DURATION);
        return createReservation(startTime, endTime, ReservationStatus.RESERVED);
    }

    public static Reservation createReservation(ReservationStatus status) {
        var startTime = fixedClock.instant();
        var endTime = startTime.plus(INITIAL_DURATION);
        return createReservation(startTime, endTime, status);
    }

    public static Reservation createReservation(Instant startTime, Instant endTime, ReservationStatus status) {
        return Reservation.create(INITIAL_USER_ID, INITIAL_ROOM_ID, INITIAL_SEAT_ID, startTime, endTime, status);
    }

    public static void stubReservationId(Reservation reservation, Long id) {
        try {
            var idField = Reservation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(reservation, id);
        } catch (ReflectiveOperationException e) {
            fail("테스트용 ID 설정 실패");
        }
    }

    public static VacancyAlertRequestCommand createVacancyAlertRequestCommand(Instant startTime, Instant endTime) {
        return new VacancyAlertRequestCommand(INITIAL_USER_ID, INITIAL_ROOM_ID, INITIAL_SEAT_ID, startTime, endTime);
    }

    public static VacancyAlertRequestCommand createVacancyAlertRequestCommand(Instant requestedAt) {
        var startTime = fixedClock.instant();
        var endTime = startTime.plus(INITIAL_DURATION);
        return createVacancyAlertRequestCommand(startTime, endTime);
    }
}
