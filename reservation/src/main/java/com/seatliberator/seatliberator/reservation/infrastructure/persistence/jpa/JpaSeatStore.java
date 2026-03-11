package com.seatliberator.seatliberator.reservation.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.reservation.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.domain.Seat;
import com.seatliberator.seatliberator.reservation.infrastructure.persistence.jpa.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaSeatStore implements SeatStore {

    private final SeatRepository repository;

    @Override
    public Seat save(Seat seat){
        return repository.save(seat);
    }

    @Override
    public Optional<Seat> findByRoomIdAndSeatId(String roomId, String seatId) {
        return repository.findByRoomIdAndSeatId(roomId, seatId);
    }

    @Override
    public void deleteByRoomIdAndSeatId(String roomId, String seatId) {
        repository.deleteByRoomIdAndSeatId(roomId, seatId);
    }

    @Override
    public boolean existsSeatConflict(String roomId, String seatId) {
        return repository.existsSeatConflict(roomId, seatId);
    }

    @Override
    public boolean existsSeatConflictExcept(String roomId, String seatId) {
        return repository.existsSeatConflictExept(roomId, seatId);
    }
}
