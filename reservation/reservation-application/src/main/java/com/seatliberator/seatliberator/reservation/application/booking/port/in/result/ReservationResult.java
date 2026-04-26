package com.seatliberator.seatliberator.reservation.application.booking.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;

import java.time.Instant;

public record ReservationResult(
        Long reservationId,
        String actorId,
        String roomId,
        String seatId,
        Instant startAt,
        Instant endAt,
        ReservationStatus status
) {
    public static ReservationResult of(Reservation reservation) {
        var locator = reservation.getLocator();
        var range = reservation.getRange();
        return new ReservationResult(
                reservation.getId(),
                reservation.getUserId(),
                locator.roomId(),
                locator.seatId(),
                range.startAt(),
                range.endAt(),
                reservation.getStatus()
        );
    }
}