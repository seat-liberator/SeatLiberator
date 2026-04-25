package com.seatliberator.seatliberator.reservation.application.room.port.in.command;

public record MoveSeatCommand(
        String oldRoomId,
        String newRoomId,
        String seatId
) {
}
