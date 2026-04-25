package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.query.ListSeatQuery;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.SeatResult;

import java.util.List;

public interface ListSeatUseCase {
    List<SeatResult> list(ListSeatQuery query);
}
