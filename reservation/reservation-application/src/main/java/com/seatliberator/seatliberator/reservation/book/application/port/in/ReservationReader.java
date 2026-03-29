package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationEntry;

public interface ReservationReader {
    ReservationEntry read(ReservationLocator reservationLocator);
}
