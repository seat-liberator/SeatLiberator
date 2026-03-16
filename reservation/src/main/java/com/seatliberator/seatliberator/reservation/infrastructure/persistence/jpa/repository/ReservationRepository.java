package com.seatliberator.seatliberator.reservation.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByUserId(String userId);

    @Query("""
            SELECT COUNT(r) > 0
            FROM Reservation r
            WHERE r.roomId = :roomId
            AND r.seatId = :seatId
            AND r.startTime < :endTime
            AND r.endTime > :startTime
            """)
    boolean existsReservationConflict(String roomId, String seatId, Instant startTime, Instant endTime);

    @Query("""
                SELECT COUNT(r) > 0
                FROM Reservation r
                WHERE r.id <> :id
                AND r.roomId = :roomId
                AND r.seatId = :seatId
                AND r.startTime < :endTime
                AND r.endTime > :startTime
            """)
    boolean existsReservationConflictExceptId(
            Long id,
            String roomId,
            String seatId,
            Instant startTime,
            Instant endTime
    );
}
