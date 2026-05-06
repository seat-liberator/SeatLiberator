package com.seatliberator.seatliberator.reservation.application.seat.port.in.query;

public record FindSeatQuery(
        String roomId,
        String seatId
) {
}
