package com.seatliberator.seatliberator.reservation.application.room.port.in.command;

public record UpdateSeatCommand(
        String roomId,
        String oldSeatId,
        String newSeatId
) {
}
