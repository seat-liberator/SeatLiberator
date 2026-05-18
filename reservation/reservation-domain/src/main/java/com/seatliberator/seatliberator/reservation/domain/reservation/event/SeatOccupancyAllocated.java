package com.seatliberator.seatliberator.reservation.domain.reservation.event;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record SeatOccupancyAllocated(
        UUID reservationId,
        Set<UUID> slotIds,
        LocalDate occupancyDate
) {
    public SeatOccupancyAllocated {
        Preconditions.requireNonNull(reservationId, "reservationId");
        Preconditions.requireNonNull(slotIds, "slotIds");
        Preconditions.requireNonNull(occupancyDate, "occupancyDate");
    }

    public static SeatOccupancyAllocated of(UUID reservationId, Set<UUID> slotIds, LocalDate occupancyDate) {
        return new SeatOccupancyAllocated(reservationId, slotIds, occupancyDate);
    }
}
