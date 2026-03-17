package com.seatliberator.seatliberator.reservation.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findByRoomIdAndSeatId(String roomId, String seatId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT s
                FROM Seat s
                WHERE s.roomId = :roomId
                AND s.seatId = :seatId
            """)
    Optional<Seat> findForUpdate(String roomId, String seatId);

    void deleteByRoomIdAndSeatId(String roomId, String seatId);

    @Query("""
            SELECT COUNT(s) > 0
            FROM Seat s
            WHERE s.roomId = :roomId
            AND s.seatId = :seatId
            """)
    boolean existsSeatConflict(String roomId, String seatId);

    @Query("""
            SELECT COUNT(s) > 0
            FROM Seat s
            WHERE s.id <> :id
            AND s.roomId = :roomId
            AND s.seatId = :seatId
            """)
    boolean existsSeatConflictExcept(Long id, String roomId, String seatId);
}
