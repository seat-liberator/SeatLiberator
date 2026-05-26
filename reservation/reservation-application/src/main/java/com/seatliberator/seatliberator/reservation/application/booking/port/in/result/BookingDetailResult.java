package com.seatliberator.seatliberator.reservation.application.booking.port.in.result;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationStateResult;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record BookingDetailResult(
        UUID reservationId,
        String userId,
        ReservationStateResult reservationState,
        List<BookingSlotResult> slots
) {
    public BookingDetailResult {
        Preconditions.requireNonNull(reservationId, "reservationId");
        Preconditions.requireNonBlank(userId, "userId");
        Preconditions.requireNonNull(reservationState, "reservationState");
        slots = List.copyOf(Preconditions.requireNonNull(slots, "slots"));
    }

    public record BookingSlotResult(
            UUID seatOccupancyId,
            UUID seatTimeSlotId,
            LocalDate occupancyDate,
            String roomCode,
            String seatCode,
            LocalTime startAt,
            Duration duration,
            SeatTimeSlotStatus status
    ) {
        public BookingSlotResult {
            Preconditions.requireNonNull(seatOccupancyId, "seatOccupancyId");
            Preconditions.requireNonNull(seatTimeSlotId, "seatTimeSlotId");
            Preconditions.requireNonNull(occupancyDate, "occupancyDate");
            Preconditions.requireNonBlank(roomCode, "roomCode");
            Preconditions.requireNonNull(seatCode, "seatCode");
            Preconditions.requireNonNull(startAt, "startAt");
            Preconditions.requireNonNull(duration, "duration");
            Preconditions.requireNonNull(status, "status");
        }
    }
}
