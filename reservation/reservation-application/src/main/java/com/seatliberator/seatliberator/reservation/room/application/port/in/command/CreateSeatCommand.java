package com.seatliberator.seatliberator.reservation.room.application.port.in.command;

public record CreateSeatCommand(
        String roomId,
        String seatId
) {
}
