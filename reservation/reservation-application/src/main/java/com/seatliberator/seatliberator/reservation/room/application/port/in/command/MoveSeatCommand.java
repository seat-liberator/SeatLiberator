package com.seatliberator.seatliberator.reservation.room.application.port.in.command;

public record MoveSeatCommand(
        String oldRoomId,
        String newRoomId,
        String seatId
) {
}
