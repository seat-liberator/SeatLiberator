package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.query.FindRoomQuery;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomResult;

public interface FindRoomUseCase {
    RoomResult find(FindRoomQuery query);
}
