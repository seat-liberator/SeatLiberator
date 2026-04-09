package com.seatliberator.seatliberator.reservation.domain.event;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.time.Instant;

public record ReservationExpired(
        SeatLocator locator,
        TimeRange range,
        Instant detectedAt
) implements DomainEvent {
}
