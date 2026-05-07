package com.seatliberator.seatliberator.reservation.application.seat.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record DeleteSeatTimeSlotCommand(
        UUID seatTimeSlotId
) {
    public DeleteSeatTimeSlotCommand {
        Preconditions.requireNonNull(seatTimeSlotId, "seatTimeSlotId");
    }
}
