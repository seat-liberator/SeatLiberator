package com.seatliberator.seatliberator.reservation.application.reservation.port.in;

import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.FindReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;

public interface FindReservationUseCase {
    ReservationResult find(FindReservationQuery query);
}
