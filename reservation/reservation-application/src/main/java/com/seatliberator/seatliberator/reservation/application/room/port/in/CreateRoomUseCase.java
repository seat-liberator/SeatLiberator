package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomResult;

public interface CreateRoomUseCase {
    RoomResult create(CreateRoomCommand command);
}
