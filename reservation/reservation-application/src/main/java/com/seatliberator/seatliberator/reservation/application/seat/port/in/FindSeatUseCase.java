package com.seatliberator.seatliberator.reservation.application.seat.port.in;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.FindSeatQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatResult;

public interface FindSeatUseCase {
    SeatResult find(FindSeatQuery query);
}
