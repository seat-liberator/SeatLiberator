package com.seatliberator.seatliberator.reservation.room.application.port.in;

import com.seatliberator.seatliberator.reservation.room.application.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.room.application.port.in.result.SeatResult;

public interface CreateSeatUseCase {
    SeatResult create(CreateSeatCommand command);
}
