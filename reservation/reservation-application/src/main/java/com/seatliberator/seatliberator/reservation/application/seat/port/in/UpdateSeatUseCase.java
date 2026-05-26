package com.seatliberator.seatliberator.reservation.application.seat.port.in;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.UpdateSeatCodeCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatResult;

public interface UpdateSeatUseCase {
    SeatResult update(UpdateSeatCodeCommand command);
}
