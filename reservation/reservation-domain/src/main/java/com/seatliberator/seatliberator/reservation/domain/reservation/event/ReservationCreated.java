package com.seatliberator.seatliberator.reservation.domain.reservation.event;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

public record ReservationCreated(
        SeatLocator locator,
        TimeRange range
) implements DomainEvent {
}
