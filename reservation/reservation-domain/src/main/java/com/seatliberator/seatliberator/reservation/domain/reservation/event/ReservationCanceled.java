package com.seatliberator.seatliberator.reservation.domain.reservation.event;

import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;

import java.time.Instant;

public record ReservationCanceled(
        SeatLocator locator,
        TimeRange range,
        Instant canceledAt
) implements DomainEvent {
}
