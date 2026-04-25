package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.DeleteRoomCommand;

public interface DeleteRoomUseCase {
    void delete(DeleteRoomCommand command);
}
