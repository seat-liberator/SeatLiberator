package com.seatliberator.seatliberator.reservation.application.availability.port.in.query;

import com.seatliberator.seatliberator.reservation.domain.shared.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;

public record FindOccupancyRangesQuery(
        SeatLocator locator,
        InstantRange range
) {
}
