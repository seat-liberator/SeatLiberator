package com.seatliberator.seatliberator.reservation.availability.application.port.in.query;

import com.seatliberator.seatliberator.reservation.domain.TimeRange;

public record FindAvailableSeatQuery(
        String roomId,
        TimeRange range
) {
}
