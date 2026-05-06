package com.seatliberator.seatliberator.reservation.application.seat.port.in;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatResult;

public interface CreateSeatUseCase {
    SeatResult create(CreateSeatCommand command);
}
