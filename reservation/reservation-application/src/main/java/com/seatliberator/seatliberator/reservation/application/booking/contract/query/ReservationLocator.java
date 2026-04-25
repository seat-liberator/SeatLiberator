package com.seatliberator.seatliberator.reservation.application.booking.contract.query;

public sealed interface ReservationLocator permits IdBasedReservationLocator, SeatBasedReservationLocator {
}
