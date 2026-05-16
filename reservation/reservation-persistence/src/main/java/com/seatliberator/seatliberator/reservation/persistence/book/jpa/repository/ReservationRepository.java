package com.seatliberator.seatliberator.reservation.persistence.book.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID>, JpaSpecificationExecutor<Reservation> {

    List<Reservation> findByUserId(String userId);

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
