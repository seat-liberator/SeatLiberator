package com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public record SeatOccupancySlotCriteria(
        Set<UUID> slotIds,
        MatchMode matchMode,
        SeatOccupancyFilter filter
) {
    public SeatOccupancySlotCriteria {
        slotIds = Set.copyOf(Preconditions.requireNonEmpty(slotIds, "slotIds"));
        Preconditions.requireNonNull(matchMode, "matchMode");
        Preconditions.requireNonNull(filter, "filter");
    }

    public static SeatOccupancySlotCriteria matchAnyOf(Collection<UUID> slotIds) {
        return matchAnyOf(Set.copyOf(slotIds));
    }

    public static SeatOccupancySlotCriteria matchAnyOf(Set<UUID> slotIds) {
        return new SeatOccupancySlotCriteria(slotIds, MatchMode.ANY_OF, SeatOccupancyFilter.empty());
    }

    public static SeatOccupancySlotCriteria matchNoneOf(Collection<UUID> slotIds) {
        return matchNoneOf(Set.copyOf(slotIds));
    }

    public static SeatOccupancySlotCriteria matchNoneOf(Set<UUID> slotIds) {
        return new SeatOccupancySlotCriteria(slotIds, MatchMode.NONE_OF, SeatOccupancyFilter.empty());
    }

    public SeatOccupancySlotCriteria filter(SeatOccupancyFilter filter) {
        return new SeatOccupancySlotCriteria(slotIds, matchMode, filter);
    }

    public enum MatchMode {
        ANY_OF,
        NONE_OF;

        public boolean isMatchAnyOf() {
            return this == ANY_OF;
        }

        public boolean isMatchNoneOf() {
            return this == NONE_OF;
        }
    }
}
