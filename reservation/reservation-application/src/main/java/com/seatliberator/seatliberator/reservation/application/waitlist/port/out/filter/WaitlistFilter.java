package com.seatliberator.seatliberator.reservation.application.waitlist.port.out.filter;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistBehavior;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistStatus;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record WaitlistFilter(
        String userId,
        WaitlistBehavior behavior,
        WaitlistStatus status,
        LocalDate occupancyDate,
        Set<UUID> slotIds
) {
    public WaitlistFilter {
        slotIds = slotIds == null ? Set.of() : Set.copyOf(slotIds);
    }

    public static WaitlistFilter empty() {
        return new WaitlistFilter(null, null, null, null, Set.of());
    }

    public WaitlistFilter userId(String userId) {
        Preconditions.requireNonBlank(userId, "userId");
        return new WaitlistFilter(userId, behavior, status, occupancyDate, slotIds);
    }

    public WaitlistFilter behavior(WaitlistBehavior behavior) {
        Preconditions.requireNonNull(behavior, "behavior");
        return new WaitlistFilter(userId, behavior, status, occupancyDate, slotIds);
    }

    public WaitlistFilter status(WaitlistStatus status) {
        Preconditions.requireNonNull(status, "status");
        return new WaitlistFilter(userId, behavior, status, occupancyDate, slotIds);
    }

    public WaitlistFilter occupancyDate(LocalDate occupancyDate) {
        Preconditions.requireNonNull(occupancyDate, "occupancyDate");
        return new WaitlistFilter(userId, behavior, status, occupancyDate, slotIds);
    }

    public WaitlistFilter slotIds(Set<UUID> slotIds) {
        Preconditions.requireNonNull(slotIds, "slotIds");
        return new WaitlistFilter(userId, behavior, status, occupancyDate, slotIds);
    }
}
