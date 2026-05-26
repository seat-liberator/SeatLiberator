package com.seatliberator.seatliberator.reservation.application.seat.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.criteria.SeatLookupCriteria;

import java.util.UUID;

public record CreateSeatCommand(
        UUID roomId,
        String seatCode
) {
    public CreateSeatCommand {
        Preconditions.requireNonNull(roomId, "roomId");
        Preconditions.requireNonBlank(seatCode, "seatCode");
    }

    public static CreateSeatCommand of(UUID roomId, String seatCode) {
        return new CreateSeatCommand(roomId, seatCode);
    }
}
