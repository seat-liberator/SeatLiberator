package com.seatliberator.seatliberator.reservation.domain.reservation.event;

import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;

import java.time.Instant;

public record ReservationExpired(
        SeatLocator locator,
        InstantRange range,
        Instant detectedAt
) implements DomainEvent {
}
