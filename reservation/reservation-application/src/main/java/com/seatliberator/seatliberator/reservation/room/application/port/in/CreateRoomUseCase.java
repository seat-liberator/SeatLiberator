package com.seatliberator.seatliberator.reservation.room.application.port.in;

import com.seatliberator.seatliberator.reservation.room.application.port.in.command.CreateRoomCommand;
import com.seatliberator.seatliberator.reservation.room.application.port.in.result.RoomResult;

public interface CreateRoomUseCase {
    RoomResult create(CreateRoomCommand command);
}
