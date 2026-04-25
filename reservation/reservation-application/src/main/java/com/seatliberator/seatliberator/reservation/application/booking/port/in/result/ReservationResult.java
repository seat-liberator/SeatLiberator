package com.seatliberator.seatliberator.reservation.application.booking.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;

public record ReservationResult(
        Long reservationId,
        String actorId,
        String roomId,
        String seatId,
        ReservationStatus status
) {
    public static ReservationResult of(Reservation reservation) {
        var locator = reservation.getLocator();
        return new ReservationResult(
                reservation.getId(),
                reservation.getUserId(),
                locator.roomId(),
                locator.seatId(),
                reservation.getStatus()
        );
    }
}