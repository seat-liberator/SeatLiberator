package com.seatliberator.seatliberator.reservation.domain.reservation.event;

import com.seatliberator.seatliberator.reservation.domain.shared.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;

public record ReservationCreated(
        SeatLocator locator,
        InstantRange range
) implements DomainEvent {
}
