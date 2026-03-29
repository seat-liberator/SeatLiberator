package com.seatliberator.seatliberator.reservation.book.application.port.in.command;

public record SeatCreateCommand(
        String roomId,
        String seatId
) {
}
