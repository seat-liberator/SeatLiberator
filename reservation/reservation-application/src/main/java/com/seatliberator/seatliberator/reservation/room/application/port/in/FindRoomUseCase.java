package com.seatliberator.seatliberator.reservation.room.application.port.in;

import com.seatliberator.seatliberator.reservation.room.application.port.in.query.FindRoomQuery;
import com.seatliberator.seatliberator.reservation.room.application.port.in.result.RoomResult;

public interface FindRoomUseCase {
    RoomResult find(FindRoomQuery query);
}
