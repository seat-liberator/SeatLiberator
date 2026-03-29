package com.seatliberator.seatliberator.reservation.book.application.port.in.command;

public record SeatUpdateCommand(
        String oldRoomId,
        String oldSeatId,
        String newRoomId,
        String newSeatId
) {
}
