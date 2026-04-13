package com.seatliberator.seatliberator.reservation.book.application.contract.query;

public sealed interface ReservationLocator permits IdBasedReservationLocator, SeatBasedReservationLocator {
}
