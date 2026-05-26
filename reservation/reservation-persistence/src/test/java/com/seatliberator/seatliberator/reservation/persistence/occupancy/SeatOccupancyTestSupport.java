package com.seatliberator.seatliberator.reservation.persistence.occupancy;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicyFixture;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DateRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDateRange;

import java.time.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SeatOccupancyTestSupport {
    public static final Clock CLOCK = TestClock.getFixed();
    public static final Instant NOW = CLOCK.instant();
    public static final UUID UNKNOWN_SEAT_OCCUPANCY_ID = UUID.fromString("00000000-0000-0000-0000-000000000401");
    public static final String SEAT_CODE = "seat-a";
    public static final String USER_ID = "user-1";
    public static final String OTHER_USER_ID = "user-2";
    public static final LocalDate OCCUPANCY_DATE = LocalDate.now(CLOCK);
    public static final LocalTime DEFAULT_SLOT_START_AT = LocalTime.of(9, 0);
    public static final Duration SLOT_DURATION = Duration.ofHours(2);

    private SeatOccupancyTestSupport() {
    }

    public static Room room() {
        return new RoomFixture.Builder()
                .code(RoomFixture.nextCode())
                .operationPolicy(new RoomOperationPolicyFixture.Builder().build())
                .build();
    }

    public static Seat seat(Room room) {
        return seat(room, SEAT_CODE);
    }

    public static Seat seat(Room room, String seatCode) {
        return Seat.of(room.getId(), seatCode, NOW);
    }

    public static SeatTimeSlot seatTimeSlot(Seat seat) {
        return seatTimeSlot(seat, DEFAULT_SLOT_START_AT);
    }

    public static SeatTimeSlot seatTimeSlot(Seat seat, LocalTime startAt) {
        var slotRange = SimpleDailyNanoRange.of(startAt, SLOT_DURATION);
        return SeatTimeSlot.of(seat.getId(), slotRange, SeatTimeSlotStatus.ACTIVE, NOW);
    }

    public static Reservation reservation(String userId) {
        return Reservation.of(userId, NOW);
    }

    public static SeatOccupancy seatOccupancy(SeatTimeSlot slot, Reservation reservation) {
        return seatOccupancy(slot, reservation, OCCUPANCY_DATE);
    }

    public static SeatOccupancy seatOccupancy(SeatTimeSlot slot, Reservation reservation, LocalDate occupancyDate) {
        return SeatOccupancy.of(slot.getId(), reservation.getId(), occupancyDate, NOW);
    }

    public static DateRange occupancyDateRange(LocalDate startDate, LocalDate endDate) {
        return SimpleDateRange.of(startDate, endDate);
    }

    public static void assertSameSeatOccupancy(SeatOccupancy actual, SeatOccupancy expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getSeatTimeSlotId()).isEqualTo(expected.getSeatTimeSlotId());
        assertThat(actual.getReservationId()).isEqualTo(expected.getReservationId());
        assertThat(actual.getOccupancyDate()).isEqualTo(expected.getOccupancyDate());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
    }
}
