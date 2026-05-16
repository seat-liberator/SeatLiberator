package com.seatliberator.seatliberator.reservation.application.reservation.port.in;

import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.FindMyReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;

import java.util.List;

public interface FindMyReservationUseCase {
    List<ReservationResult> find(FindMyReservationQuery query);
}
