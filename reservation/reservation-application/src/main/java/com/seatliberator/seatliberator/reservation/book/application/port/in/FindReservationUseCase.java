package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.contract.query.ReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.port.in.result.ReservationResult;

public interface FindReservationUseCase {
    ReservationResult find(ReservationLocator reservationLocator);
}
