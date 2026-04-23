package com.seatliberator.seatliberator.reservation.room.application.port.in;

import com.seatliberator.seatliberator.reservation.room.application.port.in.command.DeleteSeatCommand;

public interface DeleteSeatUseCase {
    void delete(DeleteSeatCommand command);
}
