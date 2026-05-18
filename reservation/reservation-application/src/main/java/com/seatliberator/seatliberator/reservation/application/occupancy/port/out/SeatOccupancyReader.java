package com.seatliberator.seatliberator.reservation.application.occupancy.port.out;

import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria.SeatOccupancySlotCriteria;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatOccupancyReader {
    boolean existsById(UUID id);

    Optional<SeatOccupancy> findById(UUID id);

    List<SeatOccupancy> findByIds(Collection<UUID> id);

    List<SeatOccupancy> findByReservationId(UUID reservationId);

    List<SeatOccupancy> findByCriteria(SeatOccupancySlotCriteria criteria);
}