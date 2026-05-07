package com.seatliberator.seatliberator.reservation.application.seat.port.in;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.ListSeatTimeSlotQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;

import java.util.List;

public interface ListSeatTimeSlotUseCase {
    List<SeatTimeSlotResult> list(ListSeatTimeSlotQuery query);
}
