package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.SeatResult;

public interface CreateSeatUseCase {
    SeatResult create(CreateSeatCommand command);
}
