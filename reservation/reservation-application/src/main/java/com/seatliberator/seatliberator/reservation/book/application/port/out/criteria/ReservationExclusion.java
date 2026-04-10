package com.seatliberator.seatliberator.reservation.book.application.port.out.criteria;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record ReservationExclusion(
        Set<Long> ids
) {
    public ReservationExclusion {
        Objects.requireNonNull(ids);
    }

    public static ReservationExclusion of(Collection<Long> ids) {
        return new ReservationExclusion(new HashSet<>(ids));
    }

    public boolean isEmpty() {
        return ids.isEmpty();
    }
}
