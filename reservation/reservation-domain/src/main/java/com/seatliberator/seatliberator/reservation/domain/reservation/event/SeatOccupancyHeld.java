package com.seatliberator.seatliberator.reservation.domain.reservation.event;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record SeatOccupancyHeld(
        UUID reservationId,
        LocalDate occupancyDate,
        Set<UUID> slotIds
) {
    public SeatOccupancyHeld {
        Preconditions.requireNonNull(reservationId, "reservationId");
        Preconditions.requireNonNull(occupancyDate, "occupancyDate");
        Preconditions.requireNonNull(slotIds, "slotIds");
    }

    public static SeatOccupancyHeld of(UUID reservationId, LocalDate occupancyDate, Set<UUID> slotIds) {
        return new SeatOccupancyHeld(reservationId, occupancyDate, slotIds);
    }
}
