package com.seatliberator.seatliberator.reservation.room.application.port.in;

import com.seatliberator.seatliberator.reservation.room.application.port.in.result.RoomResult;

import java.util.List;

public interface ListRoomUseCase {
    List<RoomResult> list();
}
