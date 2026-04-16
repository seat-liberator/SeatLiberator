package com.seatliberator.seatliberator.reservation.seat.application.port.in;

import com.seatliberator.seatliberator.reservation.seat.application.port.in.command.DeleteSeatCommand;

public interface DeleteSeatUseCase {
    boolean delete(DeleteSeatCommand command);
}
