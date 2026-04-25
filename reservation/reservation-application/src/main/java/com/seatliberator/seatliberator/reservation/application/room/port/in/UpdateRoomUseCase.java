package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomResult;

public interface UpdateRoomUseCase {
    RoomResult update(UpdateRoomCommand command);
}
