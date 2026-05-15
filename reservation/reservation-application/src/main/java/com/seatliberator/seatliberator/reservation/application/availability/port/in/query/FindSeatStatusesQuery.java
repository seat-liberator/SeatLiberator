package com.seatliberator.seatliberator.reservation.application.availability.port.in.query;

import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;

public record FindSeatStatusesQuery(
        String roomId,
        InstantRange range
) {
}
