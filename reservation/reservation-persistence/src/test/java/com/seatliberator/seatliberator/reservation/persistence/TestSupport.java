package com.seatliberator.seatliberator.reservation.persistence;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleInstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistBehavior;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class TestSupport {
    public static final Clock fixedClock = TestClock.getFixed();

    public static final String ROOM_ID = "study-room-1";
    public static final String OTHER_ROOM_ID = "study-room-2";
    public static final String SEAT_ID = "seat-a";
    public static final String OTHER_SEAT_ID = "seat-b";
    public static final String USER_ID = "user-1";
    public static final String OTHER_USER_ID = "user-2";

    public static final Duration RESERVATION_DURATION = Duration.ofMinutes(30);

    private TestSupport() {
    }

    public static Instant now() {
        return fixedClock.instant();
    }

    public static Instant reservationStartAt() {
        return now().plus(Duration.ofHours(1));
    }

    public static InstantRange reservationRange() {
        return range(reservationStartAt(), reservationStartAt().plus(RESERVATION_DURATION));
    }

    public static InstantRange overlappingRange() {
        return range(
                reservationStartAt().plus(Duration.ofMinutes(10)),
                reservationStartAt().plus(Duration.ofMinutes(20))
        );
    }

    public static InstantRange nonOverlappingRange() {
        var startAt = reservationStartAt().plus(RESERVATION_DURATION);
        return range(startAt, startAt.plus(RESERVATION_DURATION));
    }

    public static InstantRange range(Instant startAt, Instant endAt) {
        return SimpleInstantRange.of(startAt, endAt);
    }

    public static SeatLocator locator() {
        return locator(ROOM_ID, SEAT_ID);
    }

    public static SeatLocator locator(String roomId, String seatId) {
        return SimpleSeatLocator.of(roomId, seatId);
    }

    public static Room room() {
        return room(ROOM_ID);
    }

    public static Room room(String roomId) {
        return new RoomFixture.Builder()
                .roomId(roomId)
                .build();
    }

    public static Seat seat(Room room) {
        return seat(room, SEAT_ID);
    }

    public static Seat seat(Room room, String seatId) {
        return SeatFixture.create(room, seatId, now());
    }

    public static Reservation reservation() {
        return reservation(USER_ID, locator(), reservationRange(), ReservationStatus.RESERVED);
    }

    public static Reservation reservation(ReservationStatus status) {
        return reservation(USER_ID, locator(), reservationRange(), status);
    }

    public static Reservation reservation(String userId, SeatLocator locator, InstantRange range) {
        return reservation(userId, locator, range, ReservationStatus.RESERVED);
    }

    public static Reservation reservation(String userId, SeatLocator locator, InstantRange range, ReservationStatus status) {
        return Reservation.of(userId, locator, range, status);
    }

    public static Waitlist waitlist() {
        return waitlist(USER_ID, locator(), reservationRange());
    }

    public static Waitlist waitlist(String userId, SeatLocator locator, InstantRange range) {
        return waitlist(userId, locator, range, WaitlistBehavior.AUTO_CLAIM);
    }

    public static Waitlist waitlist(String userId, SeatLocator locator, InstantRange range, WaitlistBehavior behavior) {
        return Waitlist.create(userId, locator, range, behavior, range.startAt().minus(Duration.ofMinutes(1)));
    }
}
