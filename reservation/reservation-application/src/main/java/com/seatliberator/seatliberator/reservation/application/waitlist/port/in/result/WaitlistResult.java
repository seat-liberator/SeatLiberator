package com.seatliberator.seatliberator.reservation.application.waitlist.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.waitlist.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record WaitlistResult(
        UUID waitlistId,
        String userId,
        List<UUID> slotId,
        LocalDate occupancyDate,
        WaitlistBehavior behavior,
        WaitlistStateResult state
) {
    public static WaitlistResult from(Waitlist waitlist) {
        return new WaitlistResult(
                waitlist.getId(),
                waitlist.getUserId(),
                waitlist.getSlotIds(),
                waitlist.getOccupancyDate(),
                waitlist.getBehavior(),
                WaitlistStateResult.from(waitlist.getState())
        );
    }

    public record WaitlistStateResult(
            WaitlistStatus status,
            WaitlistResolution resolution,
            Instant requestedAt,
            Instant cancelledAt,
            Instant expiredAt,
            Instant failedAt,
            Instant completedAt
    ) {
        public static WaitlistStateResult from(WaitlistState state) {
            return new WaitlistStateResult(
                    state.getStatus(),
                    state.getResolution(),
                    state.getRequestedAt(),
                    state.getCancelledAt(),
                    state.getExpiredAt(),
                    state.getFailedAt(),
                    state.getCompletedAt()
            );
        }
    }
}
