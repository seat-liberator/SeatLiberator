package com.seatliberator.seatliberator.reservation.application.seat.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

public record CreateSeatTimeSlotCommand(
        UUID seatId,
        LocalTime startAt,
        Duration duration
) {
    public CreateSeatTimeSlotCommand {
        Preconditions.requireNonNull(seatId, "seatId");
        Preconditions.requireNonNull(startAt, "startAt");
        Preconditions.requireNonNull(duration, "duration");
    }

    public static CreateSeatTimeSlotCommand of(UUID seatId, LocalTime startAt, Duration duration) {
        return new CreateSeatTimeSlotCommand(seatId, startAt, duration);
    }
}
