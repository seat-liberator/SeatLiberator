package com.seatliberator.seatliberator.reservation.application.occupancy.contract;

import com.seatliberator.seatliberator.reservation.domain.reservation.event.SeatOccupancyAllocated;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record SeatOccupancyAllocatedResult(
        UUID reservationId,
        Set<UUID> slotIds,
        LocalDate occupancyDate
) {
    public static SeatOccupancyAllocatedResult of(UUID reservationId, Set<UUID> slotIds, LocalDate occupancyDate) {
        return new SeatOccupancyAllocatedResult(reservationId, slotIds, occupancyDate);
    }

    public SeatOccupancyAllocated toEvent() {
        return SeatOccupancyAllocated.of(reservationId, slotIds, occupancyDate);
    }
}
