package com.seatliberator.seatliberator.reservation.application.seat.port.in.command;

public record CreateSeatCommand(
        String roomId,
        String seatId
) {
}
