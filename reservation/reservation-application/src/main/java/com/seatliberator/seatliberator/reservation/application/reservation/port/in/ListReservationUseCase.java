package com.seatliberator.seatliberator.reservation.application.reservation.port.in;

import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.ListReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;

import java.util.List;

public interface ListReservationUseCase {
    List<ReservationResult> list(ListReservationQuery query);
}
