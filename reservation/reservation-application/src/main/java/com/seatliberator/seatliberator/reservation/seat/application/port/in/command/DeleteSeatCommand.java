package com.seatliberator.seatliberator.reservation.seat.application.port.in.command;

public record DeleteSeatCommand(
        String roomId,
        String seatId
) {
}
