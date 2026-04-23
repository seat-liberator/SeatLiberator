package com.seatliberator.seatliberator.reservation.room.application.port.in.query;

public record FindSeatQuery(
        String roomId,
        String seatId
) {
}
