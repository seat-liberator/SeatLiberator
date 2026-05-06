package com.seatliberator.seatliberator.reservation.application.seat.port.in;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.DeleteSeatCommand;

public interface DeleteSeatUseCase {
    void delete(DeleteSeatCommand command);
}
