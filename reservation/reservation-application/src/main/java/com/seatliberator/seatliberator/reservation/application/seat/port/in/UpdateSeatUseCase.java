package com.seatliberator.seatliberator.reservation.application.seat.port.in;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.UpdateSeatCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatResult;

public interface UpdateSeatUseCase {
    SeatResult update(UpdateSeatCommand command);
}
