package com.seatliberator.seatliberator.reservation.seat.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long>, JpaSpecificationExecutor<Seat> {

    Optional<Seat> findByLocator_RoomIdAndLocator_SeatId(String roomId, String seatId);

    List<Seat> findByLocator_RoomId(String roomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT s
                FROM Seat s
                WHERE s.locator.roomId = :roomId
                AND s.locator.seatId = :seatId
            """)
    Optional<Seat> findForUpdate(String roomId, String seatId);

    void deleteByLocator_RoomIdAndLocator_SeatId(String roomId, String seatId);

    @Query("""
            SELECT COUNT(s) > 0
            FROM Seat s
            WHERE s.locator.roomId = :roomId
            AND s.locator.seatId = :seatId
            """)
    boolean existsSeatConflict(String roomId, String seatId);

    @Query("""
            SELECT COUNT(s) > 0
            FROM Seat s
            WHERE s.id <> :id
            AND s.locator.roomId = :roomId
            AND s.locator.seatId = :seatId
            """)
    boolean existsSeatConflictExcept(Long id, String roomId, String seatId);
}
