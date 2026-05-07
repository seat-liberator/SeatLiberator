package com.seatliberator.seatliberator.reservation.application.seat.port.in;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.FindSeatTimeSlotQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;

public interface FindSeatTimeSlotUseCase {
    SeatTimeSlotResult find(FindSeatTimeSlotQuery query);
}
