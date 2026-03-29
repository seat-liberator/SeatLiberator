package com.seatliberator.seatliberator.reservation.book.application.port.in.entry;

import com.seatliberator.seatliberator.reservation.book.domain.Reservation;

public record ReservationEntry(
        Long reservationId,
        String actorId,
        String roomId,
        String seatId
) {
    public static ReservationEntry of(Reservation reservation) {
        return new ReservationEntry(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getRoomId(),
                reservation.getSeatId()
        );
    }
}