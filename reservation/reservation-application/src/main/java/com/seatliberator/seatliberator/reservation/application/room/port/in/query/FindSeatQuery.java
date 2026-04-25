package com.seatliberator.seatliberator.reservation.application.room.port.in.query;

public record FindSeatQuery(
        String roomId,
        String seatId
) {
}
