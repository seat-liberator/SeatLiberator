package com.seatliberator.seatliberator.reservation.application.seat.port.in;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.CreateSeatTimeSlotCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;

public interface CreateSeatTimeSlotUseCase {
    SeatTimeSlotResult create(CreateSeatTimeSlotCommand command);
}
