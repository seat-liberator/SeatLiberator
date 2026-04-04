package com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

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
}
