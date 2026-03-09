package com.seatliberator.seatliberator.reservation.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.reservation.application.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.domain.Reservation;
import com.seatliberator.seatliberator.reservation.infrastructure.persistence.jpa.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaReservationStore implements ReservationStore {

    private final ReservationRepository repository;

    @Override
    public Reservation save(Reservation reservation) {
        return repository.save(reservation);
    }

    @Override
    public Optional<Reservation> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public void delete(Reservation reservation) {
        repository.delete(reservation);
    }

    @Override
    public boolean existsSeatConflict(String roomId, String seatId, Instant startTime, Instant endTime) {
        return repository.existsSeatConflict(roomId, seatId, startTime, endTime);
    }

    @Override
    public boolean existsSeatConflictExceptId(Long id, String roomId, String seatId, Instant startTime, Instant endTime) {
        return repository.existsSeatConflictExceptId(id, roomId, seatId, startTime, endTime);
    }
}
