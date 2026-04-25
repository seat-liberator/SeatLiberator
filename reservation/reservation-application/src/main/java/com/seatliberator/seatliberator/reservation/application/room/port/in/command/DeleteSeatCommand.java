package com.seatliberator.seatliberator.reservation.application.room.port.in.command;

public record DeleteSeatCommand(
        String roomId,
        String seatId
) {
}
