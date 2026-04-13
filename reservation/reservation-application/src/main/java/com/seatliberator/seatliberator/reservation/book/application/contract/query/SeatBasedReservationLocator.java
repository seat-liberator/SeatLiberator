package com.seatliberator.seatliberator.reservation.book.application.contract.query;

import java.time.Instant;

public record SeatBasedReservationLocator(
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime
) implements ReservationLocator {
}
