package com.seatliberator.seatliberator.reservation.room.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.reservation.domain.persistence.Room;
import com.seatliberator.seatliberator.reservation.room.application.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.room.application.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.room.infrastructure.persistence.jpa.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaRoomPersistenceAdapter implements RoomStore, RoomReader {
    private final RoomRepository repository;

    @Override
    public boolean existsByRoomId(String roomId) {
        return repository.existsByRoomId(roomId);
    }

    @Override
    public Optional<Room> findByRoomId(String roomId) {
        return repository.findByRoomId(roomId);
    }

    @Override
    public List<Room> findAll() {
        return repository.findAll();
    }

    @Override
    public Room save(Room room) {
        return repository.save(room);
    }

    @Override
    public void deleteByRoomId(String roomId) {
        repository.deleteByRoomId(roomId);
    }
}
