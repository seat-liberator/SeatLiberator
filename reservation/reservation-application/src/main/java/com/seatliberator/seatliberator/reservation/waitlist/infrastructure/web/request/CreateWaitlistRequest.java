package com.seatliberator.seatliberator.reservation.waitlist.infrastructure.web.request;

import com.seatliberator.seatliberator.reservation.domain.WaitlistBehavior;

import java.time.Instant;

public record CreateWaitlistRequest(
        String roomId,
        String seatId,
        Instant startAt,
        Instant endAt,
        WaitlistBehavior behavior
) {
}
