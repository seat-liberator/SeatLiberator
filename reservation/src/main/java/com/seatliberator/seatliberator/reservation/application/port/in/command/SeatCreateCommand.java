package com.seatliberator.seatliberator.reservation.application.port.in.command;

public record SeatCreateCommand(
        String roomId,
        String seatId
) {
}
