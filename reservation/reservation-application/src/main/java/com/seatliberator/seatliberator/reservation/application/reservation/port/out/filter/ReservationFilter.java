package com.seatliberator.seatliberator.reservation.application.reservation.port.out.filter;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import org.jspecify.annotations.Nullable;

public record ReservationFilter(
        @Nullable String userId,
        @Nullable ReservationStatus status
) {
    public static ReservationFilter empty() {
        return new ReservationFilter(null, null);
    }

    public ReservationFilter userId(String userId) {
        Preconditions.requireNonBlank(userId, "userId");

        return new ReservationFilter(userId, status);
    }

    public ReservationFilter status(ReservationStatus status) {
        Preconditions.requireNonNull(status, "status");

        return new ReservationFilter(userId, status);
    }
}
