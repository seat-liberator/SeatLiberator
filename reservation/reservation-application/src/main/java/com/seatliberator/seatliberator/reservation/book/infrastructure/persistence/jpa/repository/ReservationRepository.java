package com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.reservation.book.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByUserId(String userId);

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.locator.roomId = :roomId
                AND r.locator.seatId = :seatId
                AND r.range.startAt <= :startAt
                AND :endAt < r.range.endAt""")
    Optional<Reservation> findReservationBySeatAt(
            @Param("roomId") String roomId,
            @Param("seatId") String seatId,
            @Param("startAt") Instant startAt,
            @Param("endAt") Instant endAt
    );

    @Query("""
            SELECT COUNT(r) > 0
            FROM Reservation r
            WHERE r.locator.roomId = :roomId
            AND r.locator.seatId = :seatId
            AND r.range.startAt < :endAt
            AND r.range.endAt > :startAt
            """)
    boolean existsReservationConflict(String roomId, String seatId, Instant startAt, Instant endAt);

    @Query("""
                SELECT COUNT(r) > 0
                FROM Reservation r
                WHERE r.id <> :id
                AND r.locator.roomId = :roomId
                AND r.locator.seatId = :seatId
                AND r.range.startAt < :endAt
                AND r.range.endAt > :startAt
            """)
    boolean existsReservationConflictExceptId(
            Long id,
            String roomId,
            String seatId,
            Instant startAt,
            Instant endAt
    );
}
