package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.DeleteSeatCommand;

public interface DeleteSeatUseCase {
    void delete(DeleteSeatCommand command);
}
