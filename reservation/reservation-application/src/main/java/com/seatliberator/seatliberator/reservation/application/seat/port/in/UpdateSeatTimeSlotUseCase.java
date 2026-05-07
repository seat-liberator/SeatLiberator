package com.seatliberator.seatliberator.reservation.application.seat.port.in;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.UpdateSeatTimeSlotCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;

public interface UpdateSeatTimeSlotUseCase {
    SeatTimeSlotResult update(UpdateSeatTimeSlotCommand command);
}
