package com.seatliberator.seatliberator.reservation.room.application.port.out;

import com.seatliberator.seatliberator.reservation.domain.persistence.Room;

public interface RoomStore {
    Room save(Room room);

    void deleteByRoomId(String roomId);
}
