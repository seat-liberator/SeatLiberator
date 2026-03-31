package com.seatliberator.seatliberator.reservation.book.application.port.in.entry;

import com.seatliberator.seatliberator.reservation.book.domain.Reservation;

public record ReservationEntry(
        Long reservationId,
        String actorId,
        String roomId,
        String seatId
) {
    public static ReservationEntry of(Reservation reservation) {
        var locator = reservation.getLocator();
        return new ReservationEntry(
                reservation.getId(),
                reservation.getUserId(),
                locator.roomId(),
                locator.seatId()
        );
    }
}