package com.seatliberator.seatliberator.reservation.room.application.port.in;

import com.seatliberator.seatliberator.reservation.room.application.port.in.query.FindSeatQuery;
import com.seatliberator.seatliberator.reservation.room.application.port.in.result.SeatResult;

public interface FindSeatUseCase {
    SeatResult find(FindSeatQuery query);
}
