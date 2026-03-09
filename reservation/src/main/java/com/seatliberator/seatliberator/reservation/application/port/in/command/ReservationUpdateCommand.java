package com.seatliberator.seatliberator.reservation.application.port.in.command;

import java.time.Instant;

public record ReservationUpdateCommand(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime
) {
}
