package com.seatliberator.seatliberator.reservation.seat.application.port.in.command;

public record CreateSeatCommand(
        String roomId,
        String seatId
) {
}
