package com.seatliberator.seatliberator.reservation.availability.application.port.in.result;

import com.seatliberator.seatliberator.reservation.availability.application.model.SeatReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;

public record SeatStatusesResult(
        SimpleSeatLocator locator,
        SeatReservationStatus status
) {
    public static SeatStatusesResult of(SeatLocator locator, SeatReservationStatus status) {
        return new SeatStatusesResult(SimpleSeatLocator.from(locator), status);
    }
}
