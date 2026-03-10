package com.seatliberator.seatliberator.reservation.application.port.out;

import com.seatliberator.seatliberator.reservation.domain.Reservation;

import java.time.Instant;
import java.util.Optional;

public interface ReservationStore {
    Reservation save(Reservation reservation);

    Optional<Reservation> findByUserId(String userId);

    void delete(Reservation reservation);

    boolean existsSeatConflict(String roomId, String seatId, Instant startTime, Instant endTime);

    boolean existsSeatConflictExceptId(Long id, String roomId, String seatId, Instant startTime, Instant endTime);
}
