package com.seatliberator.seatliberator.reservation.persistence.occupancy.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SeatOccupancyRepository extends JpaRepository<SeatOccupancy, UUID>, JpaSpecificationExecutor<SeatOccupancy> {
    List<SeatOccupancy> findByReservationId(UUID reservationId);
}
