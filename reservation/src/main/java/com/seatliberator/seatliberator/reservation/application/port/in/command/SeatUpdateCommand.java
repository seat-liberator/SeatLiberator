package com.seatliberator.seatliberator.reservation.application.port.in.command;

public record SeatUpdateCommand(
        String roomId,
        String seatId
) {
}
