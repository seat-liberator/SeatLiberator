package com.seatliberator.seatliberator.reservation.book.application.port.in.command;

public record CreateSeatCommand(
        String roomId,
        String seatId
) {
}
