package com.seatliberator.seatliberator.reservation.availability.application.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;

public record AvailableSeatResult(
        Long id,
        String roomId,
        String seatId
) {
    public static AvailableSeatResult from(Seat seat) {
        var locator = seat.getLocator();
        return new AvailableSeatResult(
                seat.getId(),
                locator.roomId(),
                locator.seatId()
        );
    }
}