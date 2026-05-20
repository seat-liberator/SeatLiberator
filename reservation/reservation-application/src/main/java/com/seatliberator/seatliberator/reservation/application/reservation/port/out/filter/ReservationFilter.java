package com.seatliberator.seatliberator.reservation.application.reservation.port.out.filter;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import org.jspecify.annotations.Nullable;

public record ReservationFilter(
        @Nullable String userId,
        @Nullable ReservationStateFilter state
) {
    public static ReservationFilter empty() {
        return new ReservationFilter(null, null);
    }

    public ReservationFilter userId(String userId) {
        Preconditions.requireNonBlank(userId, "userId");

        return new ReservationFilter(userId, state);
    }

    public ReservationFilter state(ReservationStateFilter state) {
        Preconditions.requireNonNull(state, "state");

        return new ReservationFilter(userId, state);
    }
}
