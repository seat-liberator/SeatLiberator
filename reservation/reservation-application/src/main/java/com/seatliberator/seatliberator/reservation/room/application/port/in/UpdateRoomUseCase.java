package com.seatliberator.seatliberator.reservation.room.application.port.in;

import com.seatliberator.seatliberator.reservation.room.application.port.in.command.UpdateRoomCommand;
import com.seatliberator.seatliberator.reservation.room.application.port.in.result.RoomResult;

public interface UpdateRoomUseCase {
    RoomResult update(UpdateRoomCommand command);
}
