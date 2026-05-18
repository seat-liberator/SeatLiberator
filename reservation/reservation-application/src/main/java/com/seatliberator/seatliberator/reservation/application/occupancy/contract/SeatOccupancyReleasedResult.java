package com.seatliberator.seatliberator.reservation.application.occupancy.contract;

import com.seatliberator.seatliberator.reservation.domain.reservation.event.SeatOccupancyReleased;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record SeatOccupancyReleasedResult(
        UUID reservationId,
        Set<UUID> slotIds,
        LocalDate occupancyDate
) {
    public static SeatOccupancyReleasedResult of(UUID reservationId, Set<UUID> slotIds, LocalDate occupancyDate) {
        return new SeatOccupancyReleasedResult(reservationId, Set.copyOf(slotIds), occupancyDate);
    }

    public SeatOccupancyReleased toEvent() {
        return SeatOccupancyReleased.of(reservationId, slotIds, occupancyDate);
    }
}
