package com.seatliberator.seatliberator.reservation.room.application.port.in;

import com.seatliberator.seatliberator.reservation.room.application.port.in.command.UpdateSeatCommand;
import com.seatliberator.seatliberator.reservation.room.application.port.in.result.SeatResult;

public interface UpdateSeatUseCase {
    SeatResult update(UpdateSeatCommand command);
}
