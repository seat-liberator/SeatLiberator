package com.seatliberator.seatliberator.reservation.room.application.port.out;

import com.seatliberator.seatliberator.reservation.domain.persistence.Room;

import java.util.Optional;

public interface RoomReader {
    boolean existsByRoomId(String roomId);

    Optional<Room> findByRoomId(String roomId);
}
