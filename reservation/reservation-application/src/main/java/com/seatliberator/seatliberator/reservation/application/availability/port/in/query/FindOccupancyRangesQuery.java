package com.seatliberator.seatliberator.reservation.application.availability.port.in.query;

import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;

public record FindOccupancyRangesQuery(
        SeatLocator locator,
        TimeRange range
) {
}
