package com.seatliberator.seatliberator.reservation.application.booking.port.in.result;

import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;

import java.util.List;

public record BookingResult(
        ReservationResult reservation,
        List<SeatTimeSlotResult> slots
) {
    public static BookingResult from(Reservation reservation, List<SeatTimeSlot> slots) {
        return new BookingResult(
                ReservationResult.from(reservation),
                slots.stream().map(SeatTimeSlotResult::from).toList()
        );
    }
}
