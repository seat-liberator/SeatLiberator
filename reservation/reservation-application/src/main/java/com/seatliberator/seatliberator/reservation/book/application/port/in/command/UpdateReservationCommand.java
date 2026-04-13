package com.seatliberator.seatliberator.reservation.book.application.port.in.command;

import java.time.Instant;

public record UpdateReservationCommand(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime
) {
}
