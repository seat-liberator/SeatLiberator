package com.seatliberator.seatliberator.reservation.application.port.in.command;

public record SeatBasedReservationLocator(
        String roomId,
        String seatId
) implements ReservationLocator {
}
