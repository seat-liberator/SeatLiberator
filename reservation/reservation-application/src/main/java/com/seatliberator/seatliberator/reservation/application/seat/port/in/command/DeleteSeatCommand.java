package com.seatliberator.seatliberator.reservation.application.seat.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record DeleteSeatCommand(UUID seatId) {
    public DeleteSeatCommand {
        Preconditions.requireNonNull(seatId, "seatId");
    }

    public static DeleteSeatCommand of(UUID seatId) {
        return new DeleteSeatCommand(seatId);
    }
}
