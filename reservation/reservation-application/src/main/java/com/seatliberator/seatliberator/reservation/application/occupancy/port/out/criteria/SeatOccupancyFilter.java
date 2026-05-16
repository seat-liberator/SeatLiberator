package com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DateRange;
import lombok.Builder;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Getter
public class SeatOccupancyFilter {
    private final Set<UUID> slotIds;
    private final DateRange range;

    @Builder
    public SeatOccupancyFilter(Collection<UUID> slotIds, DateRange range) {
        this.slotIds = slotIds == null
                ? Set.of()
                : Set.copyOf(slotIds);
        this.range = Preconditions.requireNonNull(range, "range");
    }
}