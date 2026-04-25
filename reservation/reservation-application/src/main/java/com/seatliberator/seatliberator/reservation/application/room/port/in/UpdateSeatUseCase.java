package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateSeatCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.SeatResult;

public interface UpdateSeatUseCase {
    SeatResult update(UpdateSeatCommand command);
}
