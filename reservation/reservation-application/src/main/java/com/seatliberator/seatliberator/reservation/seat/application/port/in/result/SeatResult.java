package com.seatliberator.seatliberator.reservation.seat.application.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;

public record SeatResult(
        Long id,
        String roomId,
        String seatId
) {
    public static SeatResult from(Seat seat) {
        var locator = seat.getLocator();
        return new SeatResult(
                seat.getId(),
                locator.roomId(),
                locator.seatId()
        );
    }
}