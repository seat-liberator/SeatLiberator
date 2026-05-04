package com.seatliberator.seatliberator.reservation;

import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatorCommand;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.*;

import java.time.Clock;
import java.time.Instant;

public class ReservationTestSupport {
    public static final Clock CLOCK = TestSupport.fixedClock;
    public static final Instant NOW = CLOCK.instant();
    public static final Instant START_AT = NOW;
    public static final Instant END_AT = NOW.plusSeconds(60);
    public static final TimeRange RANGE = SimpleTimeRange.of(START_AT, END_AT);
    public static final String USER_ID = "user-1";
    public static final String ROOM_ID = "room-1";
    public static final String SEAT_ID = "seat-1";
    public static final SeatLocator LOCATOR = SimpleSeatLocator.of(ROOM_ID, SEAT_ID);

    public static ReservationCreatorCommand reservationCreatorCommand() {
        return new ReservationCreatorCommand(USER_ID, LOCATOR, RANGE);
    }

    public static Reservation reservation() {
        return ReservationFixture.createReservation();
    }
}
