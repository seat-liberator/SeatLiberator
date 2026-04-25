package com.seatliberator.seatliberator.reservation.application.booking.contract.query;

public record IdBasedReservationLocator(
        Long reservationId
) implements ReservationLocator {
}
