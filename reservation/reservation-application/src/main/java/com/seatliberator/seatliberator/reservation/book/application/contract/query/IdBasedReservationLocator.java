package com.seatliberator.seatliberator.reservation.book.application.contract.query;

public record IdBasedReservationLocator(
        Long reservationId
) implements ReservationLocator {
}
