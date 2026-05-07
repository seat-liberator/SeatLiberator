package com.seatliberator.seatliberator.reservation.application.seat.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

public record UpdateSeatTimeSlotCommand(
        UUID seatTimeSlotId,
        LocalTime startAt,
        Duration duration
) {
    public UpdateSeatTimeSlotCommand {
        Preconditions.requireNonNull(seatTimeSlotId, "seatTimeSlotId");
        Preconditions.requireNonNull(startAt, "startAt");
        Preconditions.requireNonNull(duration, "duration");
    }
}
