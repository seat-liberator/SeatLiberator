package com.seatliberator.seatliberator.reservation.room.application.port.in.command;

public record UpdateSeatCommand(
        String roomId,
        String oldSeatId,
        String newSeatId
) {
}
