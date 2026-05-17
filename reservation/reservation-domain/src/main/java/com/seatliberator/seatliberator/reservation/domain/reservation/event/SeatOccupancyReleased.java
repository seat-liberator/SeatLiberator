package com.seatliberator.seatliberator.reservation.domain.reservation.event;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record SeatOccupancyReleased(
        UUID reservationId,
        LocalDate occupancyDate,
        Set<UUID> slotIds
) {
    public SeatOccupancyReleased {
        Preconditions.requireNonNull(reservationId, "reservationId");
        Preconditions.requireNonNull(occupancyDate, "occupancyDate");
        Preconditions.requireNonNull(slotIds, "slotIds");
    }

    public static SeatOccupancyReleased of(UUID reservationId, LocalDate occupancyDate, Set<UUID> slotIds) {
        return new SeatOccupancyReleased(reservationId, occupancyDate, slotIds);
    }
}
