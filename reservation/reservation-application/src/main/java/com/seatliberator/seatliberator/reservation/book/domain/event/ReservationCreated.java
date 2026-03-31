package com.seatliberator.seatliberator.reservation.book.domain.event;

import com.seatliberator.seatliberator.reservation.shared.domain.DomainEvent;
import com.seatliberator.seatliberator.reservation.shared.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.shared.domain.TimeRange;

public record ReservationCreated(
        SeatLocator locator,
        TimeRange range
) implements DomainEvent {
}
