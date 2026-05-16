package com.seatliberator.seatliberator.reservation.persistence.seat.jpa;

import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotStore;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.persistence.seat.jpa.repository.SeatTimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaSeatTimeSlotPersistenceAdapter implements SeatTimeSlotReader, SeatTimeSlotStore {
    private final SeatTimeSlotRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<SeatTimeSlot> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<SeatTimeSlot> findByIds(Collection<UUID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public List<SeatTimeSlot> findBySeatId(UUID seatId) {
        return repository.findBySeat_Id(seatId);
    }

    @Override
    public SeatTimeSlot save(SeatTimeSlot seatTimeSlot) {
        return repository.save(seatTimeSlot);
    }

    @Override
    public void delete(SeatTimeSlot seatTimeSlot) {
        repository.delete(seatTimeSlot);
    }
}
