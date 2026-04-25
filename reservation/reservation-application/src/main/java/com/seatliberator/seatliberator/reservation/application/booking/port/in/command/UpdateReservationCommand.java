package com.seatliberator.seatliberator.reservation.application.booking.port.in.command;

import java.time.Instant;

public record UpdateReservationCommand(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime
) {
}
