package com.seatliberator.seatliberator.reservation.application.reservation.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.filter.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.filter.ReservationStateFilter;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import org.jspecify.annotations.Nullable;

public record ListReservationQuery(
        String userId,
        ReservationStatus status,
        @Nullable InstantRange statusRange
) {
    public ListReservationQuery {
        Preconditions.requireNonBlank(userId, "userId");
        Preconditions.requireNonNull(status, "status");
    }

    public static ListReservationQuery of(String userId, ReservationStatus status) {
        return new ListReservationQuery(userId, status, null);
    }

    public static ListReservationQuery of(
            String userId,
            ReservationStatus status,
            @Nullable InstantRange statusRange
    ) {
        return new ListReservationQuery(userId, status, statusRange);
    }

    public ReservationFilter toFilter() {
        var stateFilter = ReservationStateFilter.status(status);
        if (statusRange != null) {
            stateFilter = stateFilter.range(statusRange);
        }

        return ReservationFilter.empty()
                .userId(userId)
                .state(stateFilter);
    }
}
