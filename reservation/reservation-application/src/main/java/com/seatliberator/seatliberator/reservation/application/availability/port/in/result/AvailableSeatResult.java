package com.seatliberator.seatliberator.reservation.application.availability.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.seat.Seat;

import java.util.UUID;

public record AvailableSeatResult(
        UUID id,
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