package com.seatliberator.seatliberator.reservation.application.seat.port.in;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.MoveSeatCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatResult;

public interface MoveSeatUseCase {
    SeatResult move(MoveSeatCommand command);
}
