package com.seatliberator.seatliberator.reservation.availability.application.port.in.result;

import com.seatliberator.seatliberator.reservation.availability.application.model.SeatReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;

public record SeatStatusesResult(
        String roomId,
        String seatId,
        SeatReservationStatus status
) {
    public static SeatStatusesResult of(SeatLocator locator, SeatReservationStatus status) {
        return new SeatStatusesResult(
                locator.roomId(),
                locator.seatId(),
                status
        );
    }
}
