package com.seatliberator.seatliberator.reservation.persistence.room.jpa;

import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.persistence.room.jpa.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaRoomPersistenceAdapter implements RoomStore, RoomReader {
    private final RoomRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }

    @Override
    public Optional<Room> findById(UUID id) {
        return repository.findById(id);
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
    public void delete(Room room) {
        repository.delete(room);
    }
}
