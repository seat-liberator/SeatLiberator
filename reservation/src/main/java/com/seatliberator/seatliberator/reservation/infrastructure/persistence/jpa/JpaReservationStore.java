package com.seatliberator.seatliberator.reservation.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.reservation.application.port.out.ReservationStore;
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
    public boolean existsReservationConflict(String roomId, String seatId, Instant startTime, Instant endTime) {
        return repository.existsReservationConflict(roomId, seatId, startTime, endTime);
    }

    @Override
    public boolean existsReservationConflictExceptId(Long id, String roomId, String seatId, Instant startTime, Instant endTime) {
        return repository.existsReservationConflictExceptId(id, roomId, seatId, startTime, endTime);
    }
}
