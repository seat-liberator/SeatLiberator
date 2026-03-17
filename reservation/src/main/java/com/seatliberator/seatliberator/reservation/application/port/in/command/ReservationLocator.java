package com.seatliberator.seatliberator.reservation.application.port.in.command;

public sealed interface ReservationLocator permits IdBasedReservationLocator, SeatBasedReservationLocator {
}
