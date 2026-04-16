package com.seatliberator.seatliberator.reservation.waitlist.application.port.in.command;

import java.util.UUID;

public record CancelWaitlistCommand(
        String userId,
        UUID alertId
) {
}
