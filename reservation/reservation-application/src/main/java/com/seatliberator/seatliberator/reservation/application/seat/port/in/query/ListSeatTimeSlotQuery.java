package com.seatliberator.seatliberator.reservation.application.seat.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;

public record ListSeatTimeSlotQuery(
        SeatLocator locator
) {
    public ListSeatTimeSlotQuery {
        Preconditions.requireNonNull(locator, "locator");
    }
}
