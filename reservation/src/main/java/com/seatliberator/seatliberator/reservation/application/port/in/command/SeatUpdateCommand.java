package com.seatliberator.seatliberator.reservation.application.port.in.command;

public record SeatUpdateCommand(
        String oldRoomId,
        String oldSeatId,
        String newRoomId,
        String newSeatId
) {
}
