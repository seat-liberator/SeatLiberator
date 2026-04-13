package com.seatliberator.seatliberator.reservation.book.application.port.in.command;

public record DeleteSeatCommand(
        String roomId,
        String seatId
) {
}
