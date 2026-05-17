package com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistBehavior;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateWaitlistCommand(
        String userId,
        List<UUID> seatTimeSlotIds,
        LocalDate occupancyDate,
        WaitlistBehavior behavior
) {
    public CreateWaitlistCommand {
        Preconditions.requireNonBlank(userId, "userId");
        Preconditions.requireNonNull(seatTimeSlotIds, "seatTimeSlotIds");
        Preconditions.requireNonNull(occupancyDate, "occupancyDate");
        Preconditions.requireNonNull(behavior, "behavior");
    }

    public static CreateWaitlistCommand of(String userId, List<UUID> seatTimeSlotIds, LocalDate occupancyDate, WaitlistBehavior behavior) {
        return new CreateWaitlistCommand(userId, seatTimeSlotIds, occupancyDate, behavior);
    }
}
