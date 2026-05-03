package com.seatliberator.seatliberator.reservation.domain.reservation.event;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.time.Instant;

public record ReservationCanceled(
        SeatLocator locator,
        TimeRange range,
        Instant canceledAt
) implements DomainEvent {
}
