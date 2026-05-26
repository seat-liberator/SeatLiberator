package com.seatliberator.seatliberator.reservation.persistence.seat;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.criteria.SeatLookupCriteria;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatFilter;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicyFixture;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SeatTestSupport {
    public static final Clock CLOCK = TestClock.getFixed();
    public static final Instant NOW = CLOCK.instant();

    public static final UUID UNKNOWN_SEAT_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");
    public static final String ROOM_CODE = "study-room-seat-a";
    public static final String OTHER_ROOM_CODE = "study-room-seat-b";
    public static final String SEAT_CODE = "seat-a";
    public static final String OTHER_SEAT_CODE = "seat-b";
    public static final String UNKNOWN_SEAT_CODE = "seat-z";

    private SeatTestSupport() {
    }

    public static Room room() {
        return room(ROOM_CODE);
    }

    public static Room otherRoom() {
        return room(OTHER_ROOM_CODE);
    }

    public static Room room(String code) {
        return new RoomFixture.Builder()
                .code(code)
                .operationPolicy(new RoomOperationPolicyFixture.Builder().build())
                .build();
    }

    public static Seat seat(Room room) {
        return seat(room, SEAT_CODE);
    }

    public static Seat otherSeat(Room room) {
        return seat(room, OTHER_SEAT_CODE);
    }

    public static Seat seat(Room room, String code) {
        return Seat.of(room.getId(), code, NOW);
    }

    public static SeatLookupCriteria lookupCriteria(Seat seat) {
        return SeatLookupCriteria.of(seat.getRoomId(), seat.getCode());
    }

    public static SeatLookupCriteria unknownSeatLookupCriteria(Room room) {
        return SeatLookupCriteria.of(room.getId(), UNKNOWN_SEAT_CODE);
    }

    public static SeatFilter roomSeatFilter(Room room) {
        return SeatFilter.empty().roomId(room.getId());
    }

    public static void assertSameSeat(Seat actual, Seat expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getRoomId()).isEqualTo(expected.getRoomId());
        assertThat(actual.getCode()).isEqualTo(expected.getCode());
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(actual.getLastActivatedAt()).isEqualTo(expected.getLastActivatedAt());
        assertThat(actual.getLastInactivatedAt()).isEqualTo(expected.getLastInactivatedAt());
    }
}
