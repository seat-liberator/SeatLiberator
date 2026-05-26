package com.seatliberator.seatliberator.reservation.application.room.port.out;

import com.seatliberator.seatliberator.reservation.domain.room.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomReader {
    boolean existsById(UUID id);

    boolean existsByCode(String code);

    Optional<Room> findById(UUID id);

    List<Room> findAll();
}
