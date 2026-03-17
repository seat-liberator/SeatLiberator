package com.seatliberator.seatliberator.reservation.application.port.in;

import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationLocator;
import com.seatliberator.seatliberator.reservation.application.port.in.entry.ReservationEntry;

public interface ReservationReader {
    ReservationEntry read(ReservationLocator reservationLocator);
}
