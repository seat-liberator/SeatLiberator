package com.seatliberator.seatliberator.reservation.application.seat.port.in.command;

public record UpdateSeatCommand(
        String roomId,
        String oldSeatId,
        String newSeatId
) {
}
