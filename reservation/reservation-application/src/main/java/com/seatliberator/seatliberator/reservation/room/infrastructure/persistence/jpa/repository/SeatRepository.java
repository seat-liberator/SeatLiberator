package com.seatliberator.seatliberator.reservation.room.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long>, JpaSpecificationExecutor<Seat> {

    Optional<Seat> findByRoom_RoomIdAndSeatId(String roomId, String seatId);

    List<Seat> findByRoom_RoomId(String roomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT s
                FROM Seat s
                WHERE s.room.roomId = :roomId
                AND s.seatId = :seatId
            """)
    Optional<Seat> findForUpdate(String roomId, String seatId);
}
