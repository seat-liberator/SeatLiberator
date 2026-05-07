package com.seatliberator.seatliberator.reservation.persistence.seat.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SeatTimeSlotRepository extends JpaRepository<SeatTimeSlot, UUID> {
    List<SeatTimeSlot> findBySeat_Id(UUID seatId);
}
