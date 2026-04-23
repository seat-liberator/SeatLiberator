package com.seatliberator.seatliberator.reservation.room.application.port.in;

import com.seatliberator.seatliberator.reservation.room.application.port.in.query.ListSeatQuery;
import com.seatliberator.seatliberator.reservation.room.application.port.in.result.SeatResult;

import java.util.List;

public interface ListSeatUseCase {
    List<SeatResult> list(ListSeatQuery query);
}
