package com.seatliberator.seatliberator.reservation.book.application.port.in.entry;

import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;

public record SeatEntry(
        Long id,
        String roomId,
        String seatId
) {
    public static SeatEntry from(Seat seat) {
        var locator = seat.getLocator();
        return new SeatEntry(
                seat.getId(),
                locator.roomId(),
                locator.seatId()
        );
    }
}