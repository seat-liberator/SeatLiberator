package com.seatliberator.seatliberator.reservation.application.reservation.port.out.filter;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import org.jspecify.annotations.Nullable;

public record ReservationStateFilter(
        ReservationStatus status,
        @Nullable InstantRange range
) {
    public ReservationStateFilter {
        Preconditions.requireNonNull(status, "status");
    }

    public static ReservationStateFilter status(ReservationStatus status) {
        return new ReservationStateFilter(status, null);
    }

    public ReservationStateFilter range(InstantRange range) {
        Preconditions.requireNonNull(range, "range");

        return new ReservationStateFilter(status, range);
    }
}
