package com.seatliberator.seatliberator.reservation.availability.application.port.in.entry;

import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;

public record AvailabilitySeatEntry(
        Long id,
        String roomId,
        String seatId
) {
    public static AvailabilitySeatEntry from(Seat seat) {
        var locator = seat.getLocator();
        return new AvailabilitySeatEntry(
                seat.getId(),
                locator.roomId(),
                locator.seatId()
        );
    }
}