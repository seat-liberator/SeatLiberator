package com.seatliberator.seatliberator.reservation.seat.application.port.in;

import com.seatliberator.seatliberator.reservation.seat.application.port.in.command.UpdateSeatCommand;

public interface UpdateSeatUseCase {
    boolean update(UpdateSeatCommand command);
}
