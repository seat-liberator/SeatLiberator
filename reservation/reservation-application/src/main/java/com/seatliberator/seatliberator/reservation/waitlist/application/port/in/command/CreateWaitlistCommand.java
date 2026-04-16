package com.seatliberator.seatliberator.reservation.waitlist.application.port.in.command;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.WaitlistBehavior;

import java.time.Instant;

public record CreateWaitlistCommand(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime,
        WaitlistBehavior behavior
) {
    public static CreateWaitlistCommand from(String userId, SeatLocator locator, TimeRange range, WaitlistBehavior behavior) {
        return new CreateWaitlistCommand(userId, locator.roomId(), locator.seatId(), range.startAt(), range.endAt(), behavior);
    }
}
