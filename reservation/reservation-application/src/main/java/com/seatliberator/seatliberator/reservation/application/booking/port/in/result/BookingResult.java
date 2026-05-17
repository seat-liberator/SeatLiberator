package com.seatliberator.seatliberator.reservation.application.booking.port.in.result;

import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;

public record BookingResult(
        ReservationResult reservation
) {
    public static BookingResult from(Reservation reservation) {
        return new BookingResult(
                ReservationResult.from(reservation)
        );
    }
}
