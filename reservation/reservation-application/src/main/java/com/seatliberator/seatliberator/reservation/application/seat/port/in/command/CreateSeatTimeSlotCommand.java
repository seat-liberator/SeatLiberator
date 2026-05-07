package com.seatliberator.seatliberator.reservation.application.seat.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;

import java.time.Duration;
import java.time.LocalTime;

public record CreateSeatTimeSlotCommand(
        SeatLocator locator,
        LocalTime startAt,
        Duration duration
) {
    public CreateSeatTimeSlotCommand {
        Preconditions.requireNonNull(locator, "locator");
        Preconditions.requireNonNull(startAt, "startAt");
        Preconditions.requireNonNull(duration, "duration");
    }
}
