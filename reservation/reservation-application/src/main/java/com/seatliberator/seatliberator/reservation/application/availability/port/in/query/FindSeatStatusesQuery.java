package com.seatliberator.seatliberator.reservation.application.availability.port.in.query;

import com.seatliberator.seatliberator.reservation.domain.TimeRange;

public record FindSeatStatusesQuery(
        String roomId,
        TimeRange range
) {
}
