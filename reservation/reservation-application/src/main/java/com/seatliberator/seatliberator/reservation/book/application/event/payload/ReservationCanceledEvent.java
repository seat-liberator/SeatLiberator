package com.seatliberator.seatliberator.reservation.book.application.event.payload;

import com.seatliberator.seatliberator.reservation.book.domain.Reservation;

import java.time.Instant;

public record ReservationCanceledEvent(
        String roomId,
        String seatId,
        Instant startAt,
        Instant endAt,
        Instant createdAt
) implements BookDomainEventPayload {
    public static ReservationCanceledEvent from(Reservation reservation, Instant createdAt) {
        var locator = reservation.getLocator();
        var range = reservation.getRange();
        return new ReservationCanceledEvent(
                locator.roomId(),
                locator.seatId(),
                range.startAt(),
                range.endAt(),
                createdAt
        );
    }
}
