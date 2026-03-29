package com.seatliberator.seatliberator.reservation.book.application.port.in.command;

public record IdBasedReservationLocator(
        Long reservationId
) implements ReservationLocator {
}
