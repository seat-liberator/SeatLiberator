package com.seatliberator.seatliberator.reservation.persistence;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleInstantRange;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistBehavior;

import java.time.*;
import java.util.List;
import java.util.UUID;

public class TestSupport {
    public static final Clock fixedClock = TestClock.getFixed();

    public static final String ROOM_ID = "study-room-1";
    public static final String OTHER_ROOM_ID = "study-room-2";
    public static final String SEAT_ID = "seat-a";
    public static final String OTHER_SEAT_ID = "seat-b";
    public static final String USER_ID = "user-1";
    public static final String OTHER_USER_ID = "user-2";
    public static final UUID SLOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID OTHER_SLOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

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
        return reservation(USER_ID, ReservationStatus.RESERVED);
    }

    public static Reservation reservation(ReservationStatus status) {
        return reservation(USER_ID, status);
    }

    public static Reservation reservation(String userId) {
        return reservation(userId, ReservationStatus.RESERVED);
    }

    public static Reservation reservation(String userId, ReservationStatus status) {
        var reservation = Reservation.of(userId, reservationStartAt());

        switch (status) {
            case RESERVED -> {
            }
            case USED -> reservation.use(reservationStartAt().plus(Duration.ofMinutes(10)));
            case CANCELLED -> reservation.cancel(reservationStartAt().minus(Duration.ofMinutes(10)));
            case EXPIRED -> reservation.expire(reservationStartAt().minus(Duration.ofMinutes(10)));
        }

        return reservation;
    }

    public static Waitlist waitlist() {
        return waitlist(USER_ID, List.of(SLOT_ID), occupancyDate(), WaitlistBehavior.AUTO_CLAIM);
    }

    public static Waitlist waitlist(String userId, SeatLocator locator, InstantRange range) {
        return waitlist(userId, slotIds(locator), occupancyDate(range), WaitlistBehavior.AUTO_CLAIM);
    }

    public static Waitlist waitlist(String userId, SeatLocator locator, InstantRange range, WaitlistBehavior behavior) {
        return waitlist(userId, slotIds(locator), occupancyDate(range), behavior);
    }

    public static Waitlist waitlist(String userId, List<UUID> slotIds, LocalDate occupancyDate) {
        return waitlist(userId, slotIds, occupancyDate, WaitlistBehavior.AUTO_CLAIM);
    }

    public static Waitlist waitlist(String userId, List<UUID> slotIds, LocalDate occupancyDate, WaitlistBehavior behavior) {
        return Waitlist.of(userId, slotIds, occupancyDate, behavior, reservationStartAt().minus(Duration.ofMinutes(1)));
    }

    public static LocalDate occupancyDate() {
        return occupancyDate(reservationRange());
    }

    private static LocalDate occupancyDate(InstantRange range) {
        return range.startAt().atZone(ZoneOffset.UTC).toLocalDate();
    }

    private static List<UUID> slotIds(SeatLocator locator) {
        if (OTHER_SEAT_ID.equals(locator.seatId())) {
            return List.of(OTHER_SLOT_ID);
        }
        return List.of(SLOT_ID);
    }
}
