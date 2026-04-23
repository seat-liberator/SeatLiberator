package com.seatliberator.seatliberator.reservation.room.application.port.in;

import com.seatliberator.seatliberator.reservation.room.application.port.in.command.MoveSeatCommand;
import com.seatliberator.seatliberator.reservation.room.application.port.in.result.SeatResult;

public interface MoveSeatUseCase {
    SeatResult move(MoveSeatCommand command);
}
