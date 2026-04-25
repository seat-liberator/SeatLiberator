package com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command;

import java.util.UUID;

public record CancelWaitlistCommand(
        String userId,
        UUID waitlistId
) {
}
