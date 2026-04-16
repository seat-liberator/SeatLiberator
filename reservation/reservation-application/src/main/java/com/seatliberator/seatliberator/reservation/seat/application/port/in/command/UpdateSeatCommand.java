package com.seatliberator.seatliberator.reservation.seat.application.port.in.command;

public record UpdateSeatCommand(
        String oldRoomId,
        String oldSeatId,
        String newRoomId,
        String newSeatId
) {
}
