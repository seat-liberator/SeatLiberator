package com.seatliberator.seatliberator.reservation;

import com.seatliberator.seatliberator.reservation.domain.Reservation;

import java.time.Duration;
import java.time.Instant;

public class ReservationTestFixture {
    public static final String INITIAL_USER_ID = "user-1";
    public static final String INITIAL_ROOM_ID = "room-1";
    public static final String INITIAL_SEAT_ID = "seat-1";
    public static final Duration INITIAL_DURATION = Duration.ofMinutes(60);

    public static Reservation createReservation(
            Instant startTime,
            Instant endTime
    ) {
        return Reservation.create(
                INITIAL_USER_ID,
                INITIAL_ROOM_ID,
                INITIAL_SEAT_ID,
                startTime,
                endTime
        );
    }

    public static Reservation createReservation(Instant startTime) {
        Instant endTime = startTime.plus(INITIAL_DURATION);
        return createReservation(startTime, endTime);
    }

    public static Reservation createReservation(Instant startTime, Duration duration) {
        Instant endTime = startTime.plus(duration);
        return createReservation(startTime, endTime);
    }
}
