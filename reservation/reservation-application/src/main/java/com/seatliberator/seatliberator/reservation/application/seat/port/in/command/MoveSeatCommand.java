package com.seatliberator.seatliberator.reservation.application.seat.port.in.command;

public record MoveSeatCommand(
        String oldRoomId,
        String newRoomId,
        String seatId
) {
}
