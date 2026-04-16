package com.seatliberator.seatliberator.reservation.waitlist.application.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.persistence.Waitlist;

public record WaitlistResult(
        String userId,
        SimpleSeatLocator locator,
        SimpleTimeRange range
) {
    public static WaitlistResult from(Waitlist request) {
        return new WaitlistResult(
                request.getUserId(),
                SimpleSeatLocator.of(request.getLocator()),
                SimpleTimeRange.of(request.getRange())
        );
    }
}
