package com.seatliberator.seatliberator.reservation.book.application.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;

public record ReservationResult(
        Long reservationId,
        String actorId,
        String roomId,
        String seatId
) {
    public static ReservationResult of(Reservation reservation) {
        var locator = reservation.getLocator();
        return new ReservationResult(
                reservation.getId(),
                reservation.getUserId(),
                locator.roomId(),
                locator.seatId()
        );
    }
}