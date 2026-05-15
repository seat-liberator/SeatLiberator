package com.seatliberator.seatliberator.reservation.domain.reservation.event;

import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;

public record ReservationCreated(
        SeatLocator locator,
        InstantRange range
) implements DomainEvent {
}
