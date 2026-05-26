package com.seatliberator.seatliberator.reservation.persistence.booking.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.persistence.booking.jpa.row.BookingDetailRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookingDetailRepository extends Repository<Reservation, UUID> {
    @Query("""
            SELECT
                reservation.id AS reservationId,
                reservation.userId AS userId,
                reservation.state.status AS status,
                reservation.state.reservedAt AS reservedAt,
                reservation.state.usedAt AS usedAt,
                reservation.state.cancelledAt AS cancelledAt,
                reservation.state.expiredAt AS expiredAt,
                occupancy.id AS occupancyId,
                occupancy.occupancyDate AS occupancyDate,
                slot.id AS seatTimeSlotId,
                room.code AS roomCode,
                seat.code AS seatCode,
                slot.slotRange.startNanoOfDay AS slotStartNanoOfDay,
                slot.slotRange.endNanoOfDay AS slotEndNanoOfDay,
                slot.slotStatus AS slotStatus
            FROM Reservation reservation
            LEFT JOIN SeatOccupancy occupancy ON occupancy.reservationId = reservation.id
            LEFT JOIN occupancy.seatTimeSlot slot
            LEFT JOIN slot.seat seat
            LEFT JOIN seat.room room
            WHERE reservation.id = :reservationId
            ORDER BY occupancy.occupancyDate ASC, slot.slotRange.startNanoOfDay ASC, slot.id ASC
            """)
    List<BookingDetailRow> findRowsByReservationId(@Param("reservationId") UUID reservationId);
}
