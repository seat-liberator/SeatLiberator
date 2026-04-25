package com.seatliberator.seatliberator.reservation.application.booking.contract.query;

import java.time.Instant;

public record SeatBasedReservationLocator(
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime
) implements ReservationLocator {
}
