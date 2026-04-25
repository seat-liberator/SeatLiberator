package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.query.FindSeatQuery;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.SeatResult;

public interface FindSeatUseCase {
    SeatResult find(FindSeatQuery query);
}
