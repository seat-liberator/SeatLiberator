package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomCodeCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomResult;

public interface UpdateRoomCodeUseCase {
    RoomResult update(UpdateRoomCodeCommand command);
}
