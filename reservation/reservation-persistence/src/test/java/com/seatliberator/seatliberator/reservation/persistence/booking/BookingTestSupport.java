package com.seatliberator.seatliberator.reservation.persistence.booking;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingDetailResult;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicyFixture;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailyNanoRange;

import java.time.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingTestSupport {
    public static final Clock CLOCK = TestClock.getFixed();
    public static final Instant NOW = CLOCK.instant();
    public static final UUID UNKNOWN_RESERVATION_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    public static final String USER_ID = "user-1";
    public static final String OTHER_USER_ID = "user-2";
    public static final String SEAT_CODE = "seat-a";
    public static final LocalDate OCCUPANCY_DATE = LocalDate.now(CLOCK);
    public static final Duration SLOT_DURATION = Duration.ofHours(2);

    private BookingTestSupport() {
    }

    public static Room room() {
        return new RoomFixture.Builder()
                .code(RoomFixture.nextCode())
                .operationPolicy(new RoomOperationPolicyFixture.Builder().build())
                .build();
    }

    public static Seat seat(Room room) {
        return Seat.of(room.getId(), SEAT_CODE, NOW);
    }

    public static SeatTimeSlot seatTimeSlot(Seat seat, LocalTime startAt) {
        var slotRange = SimpleDailyNanoRange.of(startAt, SLOT_DURATION);
        return SeatTimeSlot.of(seat.getId(), slotRange, SeatTimeSlotStatus.ACTIVE, NOW);
    }

    public static Reservation reservation(String userId) {
        return Reservation.of(userId, NOW);
    }

    public static SeatOccupancy seatOccupancy(SeatTimeSlot slot, Reservation reservation, LocalDate occupancyDate) {
        return SeatOccupancy.of(slot.getId(), reservation.getId(), occupancyDate, NOW);
    }

    public static void assertReservation(BookingDetailResult actual, Reservation expected) {
        assertThat(actual.reservationId()).isEqualTo(expected.getId());
        assertThat(actual.userId()).isEqualTo(expected.getUserId());
        assertThat(actual.reservationState().status()).isEqualTo(ReservationStatus.RESERVED);
        assertThat(actual.reservationState().reservedAt()).isEqualTo(expected.getState().getReservedAt());
        assertThat(actual.reservationState().usedAt()).isNull();
        assertThat(actual.reservationState().cancelledAt()).isNull();
        assertThat(actual.reservationState().expiredAt()).isNull();
    }

    public static void assertSlot(
            BookingDetailResult.BookingSlotResult actual,
            SeatOccupancy expectedOccupancy,
            SeatTimeSlot expectedSlot,
            Room expectedRoom,
            Seat expectedSeat,
            LocalTime expectedStartAt
    ) {
        assertThat(actual.seatOccupancyId()).isEqualTo(expectedOccupancy.getId());
        assertThat(actual.seatTimeSlotId()).isEqualTo(expectedSlot.getId());
        assertThat(actual.occupancyDate()).isEqualTo(expectedOccupancy.getOccupancyDate());
        assertThat(actual.roomCode()).isEqualTo(expectedRoom.getCode());
        assertThat(actual.seatCode()).isEqualTo(expectedSeat.getCode());
        assertThat(actual.startAt()).isEqualTo(expectedStartAt);
        assertThat(actual.duration()).isEqualTo(SLOT_DURATION);
        assertThat(actual.status()).isEqualTo(SeatTimeSlotStatus.ACTIVE);
    }
}
