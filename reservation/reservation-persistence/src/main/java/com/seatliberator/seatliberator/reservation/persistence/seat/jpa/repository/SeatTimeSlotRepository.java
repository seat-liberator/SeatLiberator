package com.seatliberator.seatliberator.reservation.persistence.seat.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SeatTimeSlotRepository extends JpaRepository<SeatTimeSlot, UUID>, JpaSpecificationExecutor<SeatTimeSlot> {
}
