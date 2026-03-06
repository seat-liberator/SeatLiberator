package com.seatliberator.seatliberator.reservation.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reservation> findByUserId(String userId);

    @Query("""
            SELECT COUNT(r) > 0
            FROM Reservation r
            WHERE r.roomId = :roomId
            AND r.seatId = :seatId
            AND r.startTime < :endTime
            AND r.endTime > :startTime
            """)
    boolean existsSeatConflict(String roomId, String seatId, Instant startTime, Instant endTime);
}
