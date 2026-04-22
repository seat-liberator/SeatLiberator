package com.seatliberator.seatliberator.reservation.room.application.port.in;

import com.seatliberator.seatliberator.reservation.room.application.port.in.command.DeleteRoomCommand;

public interface DeleteRoomUseCase {
    void delete(DeleteRoomCommand command);
}
