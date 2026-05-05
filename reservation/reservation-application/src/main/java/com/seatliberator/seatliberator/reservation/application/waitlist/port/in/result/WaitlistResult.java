package com.seatliberator.seatliberator.reservation.application.waitlist.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.shared.SimpleInstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;

public record WaitlistResult(
        String userId,
        SimpleSeatLocator locator,
        SimpleInstantRange range
) {
    public static WaitlistResult from(Waitlist request) {
        return new WaitlistResult(
                request.getUserId(),
                SimpleSeatLocator.from(request.getLocator()),
                SimpleInstantRange.from(request.getRange())
        );
    }
}
