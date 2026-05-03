package com.seatliberator.seatliberator.reservation.persistence.room.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    boolean existsByRoomId(String roomId);

    Optional<Room> findByRoomId(String roomId);

    void deleteByRoomId(String roomId);
}
