package com.seatliberator.seatliberator.reservation.persistence.seat;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatTimeSlotFilter;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatTimeSlotRangeOverlapCriteria;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicyFixture;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailyNanoRange;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SeatTimeSlotTestSupport {
    public static final Clock CLOCK = TestClock.getFixed();
    public static final Instant NOW = CLOCK.instant();

    public static final String ROOM_CODE = "study-room-seat-time-slot-a";
    public static final String SEAT_CODE = "seat-a";
    public static final String OTHER_SEAT_CODE = "seat-b";
    public static final UUID UNKNOWN_SEAT_TIME_SLOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    public static final LocalTime SLOT_START_AT = LocalTime.of(9, 0);
    public static final Duration SLOT_DURATION = Duration.ofHours(2);
    public static final LocalTime OTHER_SLOT_START_AT = LocalTime.of(13, 0);
    public static final Duration OTHER_SLOT_DURATION = Duration.ofHours(2);
    public static final LocalTime OVERLAPPING_SLOT_START_AT = LocalTime.of(10, 0);
    public static final Duration OVERLAPPING_SLOT_DURATION = Duration.ofMinutes(30);
    public static final LocalTime NON_OVERLAPPING_SLOT_START_AT = LocalTime.of(11, 0);
    public static final Duration NON_OVERLAPPING_SLOT_DURATION = Duration.ofHours(1);

    private SeatTimeSlotTestSupport() {
    }

    public static Room room() {
        return new RoomFixture.Builder()
                .code(ROOM_CODE)
                .operationPolicy(new RoomOperationPolicyFixture.Builder().build())
                .build();
    }

    public static Seat seat(Room room) {
        return Seat.of(room.getId(), SEAT_CODE, NOW);
    }

    public static Seat otherSeat(Room room) {
        return Seat.of(room.getId(), OTHER_SEAT_CODE, NOW);
    }

    public static SeatTimeSlot seatTimeSlot(Seat seat) {
        return seatTimeSlot(seat, slotRange());
    }

    public static SeatTimeSlot otherSeatTimeSlot(Seat seat) {
        return seatTimeSlot(seat, otherSlotRange());
    }

    public static SeatTimeSlot seatTimeSlot(Seat seat, DailyNanoRange slotRange) {
        return SeatTimeSlot.of(seat.getId(), slotRange, SeatTimeSlotStatus.ACTIVE, NOW);
    }

    public static DailyNanoRange slotRange() {
        return SimpleDailyNanoRange.of(SLOT_START_AT, SLOT_DURATION);
    }

    public static DailyNanoRange otherSlotRange() {
        return SimpleDailyNanoRange.of(OTHER_SLOT_START_AT, OTHER_SLOT_DURATION);
    }

    public static DailyNanoRange overlappingSlotRange() {
        return SimpleDailyNanoRange.of(OVERLAPPING_SLOT_START_AT, OVERLAPPING_SLOT_DURATION);
    }

    public static DailyNanoRange nonOverlappingSlotRange() {
        return SimpleDailyNanoRange.of(NON_OVERLAPPING_SLOT_START_AT, NON_OVERLAPPING_SLOT_DURATION);
    }

    public static SeatTimeSlotFilter seatTimeSlotFilter(Seat seat) {
        return SeatTimeSlotFilter.empty().seatId(seat.getId());
    }

    public static SeatTimeSlotRangeOverlapCriteria overlappingCriteria(Seat seat) {
        return SeatTimeSlotRangeOverlapCriteria.of(overlappingSlotRange(), seatTimeSlotFilter(seat));
    }

    public static SeatTimeSlotRangeOverlapCriteria nonOverlappingCriteria(Seat seat) {
        return SeatTimeSlotRangeOverlapCriteria.of(nonOverlappingSlotRange(), seatTimeSlotFilter(seat));
    }

    public static void assertSameSeatTimeSlot(SeatTimeSlot actual, SeatTimeSlot expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getSeatId()).isEqualTo(expected.getSeatId());
        assertThat(actual.getSlotRange().startNanoOfDay()).isEqualTo(expected.getSlotRange().startNanoOfDay());
        assertThat(actual.getSlotRange().endNanoOfDay()).isEqualTo(expected.getSlotRange().endNanoOfDay());
        assertThat(actual.getSlotStatus()).isEqualTo(expected.getSlotStatus());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(actual.getLastActivatedAt()).isEqualTo(expected.getLastActivatedAt());
        assertThat(actual.getLastInactivatedAt()).isEqualTo(expected.getLastInactivatedAt());
    }
}
