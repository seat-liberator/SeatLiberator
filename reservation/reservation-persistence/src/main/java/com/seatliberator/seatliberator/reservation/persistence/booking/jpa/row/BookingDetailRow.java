package com.seatliberator.seatliberator.reservation.persistence.booking.jpa.row;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingDetailResult;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationStateResult;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailyNanoRange;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BookingDetailRow(
        UUID reservationId,
        String userId,
        ReservationStatus status,
        Instant reservedAt,
        Instant usedAt,
        Instant cancelledAt,
        Instant expiredAt,
        UUID occupancyId,
        LocalDate occupancyDate,
        UUID seatTimeSlotId,
        String roomCode,
        String seatCode,
        Long slotStartNanoOfDay,
        Long slotEndNanoOfDay,
        SeatTimeSlotStatus slotStatus
) {
    public ReservationStateResult toReservationStateResult() {
        return new ReservationStateResult(status, reservedAt, usedAt, cancelledAt, expiredAt);
    }

    public BookingDetailResult.BookingSlotResult toSlotResult() {
        var range = SimpleDailyNanoRange.of(slotStartNanoOfDay, slotEndNanoOfDay);
        return new BookingDetailResult.BookingSlotResult(
                occupancyId,
                seatTimeSlotId,
                occupancyDate,
                roomCode,
                seatCode,
                range.startAt(),
                range.duration(),
                slotStatus
        );
    }
}