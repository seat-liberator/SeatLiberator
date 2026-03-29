package com.seatliberator.seatliberator.reservation.book.application.port.in.command;

public sealed interface ReservationLocator permits IdBasedReservationLocator, SeatBasedReservationLocator {
}
