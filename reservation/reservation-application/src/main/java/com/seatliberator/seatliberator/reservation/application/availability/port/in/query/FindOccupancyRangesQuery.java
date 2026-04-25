package com.seatliberator.seatliberator.reservation.application.availability.port.in.query;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

public record FindOccupancyRangesQuery(
        SeatLocator locator,
        TimeRange range
) {
}
