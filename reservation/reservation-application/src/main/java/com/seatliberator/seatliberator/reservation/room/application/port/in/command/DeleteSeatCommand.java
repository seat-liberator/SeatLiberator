package com.seatliberator.seatliberator.reservation.room.application.port.in.command;

public record DeleteSeatCommand(
        String roomId,
        String seatId
) {
}
