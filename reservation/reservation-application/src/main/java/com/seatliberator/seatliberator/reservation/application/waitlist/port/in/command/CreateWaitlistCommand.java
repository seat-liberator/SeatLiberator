package com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command;

import com.seatliberator.seatliberator.reservation.domain.shared.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistBehavior;

import java.time.Instant;

public record CreateWaitlistCommand(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime,
        WaitlistBehavior behavior
) {
    public static CreateWaitlistCommand from(String userId, SeatLocator locator, InstantRange range, WaitlistBehavior behavior) {
        return new CreateWaitlistCommand(userId, locator.roomId(), locator.seatId(), range.startAt(), range.endAt(), behavior);
    }
}
