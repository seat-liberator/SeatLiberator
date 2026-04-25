package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomResult;

import java.util.List;

public interface ListRoomUseCase {
    List<RoomResult> list();
}
