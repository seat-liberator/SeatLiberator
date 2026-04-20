package com.seatliberator.seatliberator.reservation.availability.application.port.in.query;

import com.seatliberator.seatliberator.reservation.domain.TimeRange;

public record FindSeatStatusesQuery(
        String roomId,
        TimeRange range
) {
}
