package com.seatliberator.seatliberator.reservation.application.reservation.port.in;

import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.ListMyReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;

import java.util.List;

public interface ListMyReservationUseCase {
    List<ReservationResult> list(ListMyReservationQuery query);
}
