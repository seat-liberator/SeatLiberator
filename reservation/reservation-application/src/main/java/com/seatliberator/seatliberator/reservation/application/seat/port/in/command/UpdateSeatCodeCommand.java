package com.seatliberator.seatliberator.reservation.application.seat.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdateSeatCodeCommand(
        UUID seatId,
        String newCode
) {
    public UpdateSeatCodeCommand {
        Preconditions.requireNonNull(seatId, "seatId");
        Preconditions.requireNonBlank(newCode, "newCode");
    }

    public static UpdateSeatCodeCommand of(UUID seatId, String newCode) {
        return new UpdateSeatCodeCommand(seatId, newCode);
    }
}
