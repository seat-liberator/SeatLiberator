package com.seatliberator.seatliberator.reservation.application.room.port.in.command;

public record CreateSeatCommand(
        String roomId,
        String seatId
) {
}
