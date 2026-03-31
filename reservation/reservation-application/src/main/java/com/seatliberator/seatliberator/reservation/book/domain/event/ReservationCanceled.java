package com.seatliberator.seatliberator.reservation.book.domain.event;

import com.seatliberator.seatliberator.reservation.shared.domain.DomainEvent;
import com.seatliberator.seatliberator.reservation.shared.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.shared.domain.TimeRange;

import java.time.Instant;

public record ReservationCanceled(
        SeatLocator locator,
        TimeRange range,
        Instant canceledAt
) implements DomainEvent {
}
