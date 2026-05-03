package com.seatliberator.seatliberator.reservation.domain.reservation.event;

import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;

public record ReservationCreated(
        SeatLocator locator,
        TimeRange range
) implements DomainEvent {
}
