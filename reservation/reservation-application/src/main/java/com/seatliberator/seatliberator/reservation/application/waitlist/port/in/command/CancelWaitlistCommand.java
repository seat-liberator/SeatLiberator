package com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record CancelWaitlistCommand(UUID waitlistId) {
    public CancelWaitlistCommand {
        Preconditions.requireNonNull(waitlistId, "waitlistId");
    }

    public static CancelWaitlistCommand of(UUID waitlistId) {
        return new CancelWaitlistCommand(waitlistId);
    }
}
