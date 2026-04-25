package com.seatliberator.seatliberator.reservation.application.room.port.out.criteria;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record SeatExclusion(
        Set<Long> ids
) {
    public SeatExclusion {
        Objects.requireNonNull(ids);
    }

    public static SeatExclusion of(Collection<Long> ids) {
        return new SeatExclusion(new HashSet<>(ids));
    }

    public boolean isEmpty() {
        return ids.isEmpty();
    }
}
