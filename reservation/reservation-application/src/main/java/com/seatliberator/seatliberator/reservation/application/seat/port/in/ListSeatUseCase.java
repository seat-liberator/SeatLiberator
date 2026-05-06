package com.seatliberator.seatliberator.reservation.application.seat.port.in;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.ListSeatQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatResult;

import java.util.List;

public interface ListSeatUseCase {
    List<SeatResult> list(ListSeatQuery query);
}
