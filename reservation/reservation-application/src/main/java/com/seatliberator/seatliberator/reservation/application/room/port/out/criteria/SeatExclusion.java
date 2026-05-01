package com.seatliberator.seatliberator.reservation.application.room.port.out.criteria;

import java.util.*;

public record SeatExclusion(
        Set<UUID> ids
) {
    public SeatExclusion {
        Objects.requireNonNull(ids);
    }

    public static SeatExclusion of(Collection<UUID> ids) {
        return new SeatExclusion(new HashSet<>(ids));
    }

    public boolean isEmpty() {
        return ids.isEmpty();
    }
}
