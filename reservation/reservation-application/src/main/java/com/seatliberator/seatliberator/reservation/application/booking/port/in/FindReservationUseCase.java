package com.seatliberator.seatliberator.reservation.application.booking.port.in;

import com.seatliberator.seatliberator.reservation.application.booking.contract.query.ReservationLocator;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.ReservationResult;

public interface FindReservationUseCase {
    ReservationResult find(ReservationLocator reservationLocator);
}
