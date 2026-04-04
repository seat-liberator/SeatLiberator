package com.seatliberator.seatliberator.reservation.domain.fixture;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;

import java.time.Duration;
import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Fail.fail;

public class ReservationFixture {
    public static final String INITIAL_USER_ID = "user-1";
    public static final String INITIAL_ROOM_ID = "room-1";
    public static final String INITIAL_SEAT_ID = "seat-1";
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
}
