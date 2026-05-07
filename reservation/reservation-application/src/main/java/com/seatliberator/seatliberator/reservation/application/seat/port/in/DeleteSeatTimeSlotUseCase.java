package com.seatliberator.seatliberator.reservation.application.seat.port.in;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.DeleteSeatTimeSlotCommand;

public interface DeleteSeatTimeSlotUseCase {
    void delete(DeleteSeatTimeSlotCommand command);
}
