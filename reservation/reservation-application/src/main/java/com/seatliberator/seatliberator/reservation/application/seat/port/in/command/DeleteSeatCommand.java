package com.seatliberator.seatliberator.reservation.application.seat.port.in.command;

public record DeleteSeatCommand(
        String roomId,
        String seatId
) {
}
