package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.MoveSeatCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.SeatResult;

public interface MoveSeatUseCase {
    SeatResult move(MoveSeatCommand command);
}
