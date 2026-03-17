package com.seatliberator.seatliberator.reservation.application.port.in.command;

public record IdBasedReservationLocator(
        Long reservationId
) implements ReservationLocator {
}
