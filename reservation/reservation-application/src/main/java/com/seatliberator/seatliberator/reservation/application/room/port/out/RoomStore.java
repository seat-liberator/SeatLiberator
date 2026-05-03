package com.seatliberator.seatliberator.reservation.application.room.port.out;

import com.seatliberator.seatliberator.reservation.domain.room.Room;

public interface RoomStore {
    Room save(Room room);

    void deleteByRoomId(String roomId);
}
