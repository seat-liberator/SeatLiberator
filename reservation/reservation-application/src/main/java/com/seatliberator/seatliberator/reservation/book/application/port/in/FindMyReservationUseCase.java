package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.query.FindMyReservationQuery;
import com.seatliberator.seatliberator.reservation.book.application.port.in.result.ReservationResult;

import java.util.List;

public interface FindMyReservationUseCase {
    List<ReservationResult> find(FindMyReservationQuery query);
}
