package com.seatliberator.seatliberator.reservation.domain.waitlist;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;

public class WaitlistFixture {
    public static final String INITIAL_USER_ID = "user-1";
    public static final UUID INITIAL_SLOT_ID = new UUID(0L, 1L);
    public static final UUID NEXT_SLOT_ID = new UUID(0L, 2L);
    public static final List<UUID> INITIAL_SLOT_IDS = List.of(INITIAL_SLOT_ID, NEXT_SLOT_ID);
    public static final LocalDate INITIAL_OCCUPANCY_DATE = LocalDate.of(2026, 1, 1);

    public static Waitlist createWaitlist() {
        return createWaitlist(fixedClock.instant());
    }

    public static Waitlist createWaitlist(Instant requestedAt) {
        return Waitlist.autoClaim(
                INITIAL_USER_ID,
                INITIAL_SLOT_IDS,
                INITIAL_OCCUPANCY_DATE,
                requestedAt
        );
    }

    public static Waitlist createWaitlist(WaitlistBehavior behavior) {
        return Waitlist.of(
                INITIAL_USER_ID,
                INITIAL_SLOT_IDS,
                INITIAL_OCCUPANCY_DATE,
                behavior,
                fixedClock.instant()
        );
    }

    public static Waitlist createWaitlist(WaitlistStatus status) {
        return createWaitlist(status, WaitlistBehavior.AUTO_CLAIM);
    }

    public static Waitlist createWaitlist(WaitlistStatus status, WaitlistBehavior behavior) {
        var waitlist = createWaitlist(behavior);
        transitionTo(waitlist, status, fixedClock.instant().plusSeconds(1));
        return waitlist;
    }

    private static void transitionTo(Waitlist waitlist, WaitlistStatus status, Instant at) {
        switch (status) {
            case ACTIVE -> {
            }
            case CANCELLED -> waitlist.cancel(at);
            case EXPIRED -> waitlist.expire(at);
            case FAILED -> waitlist.fail(at);
            case COMPLETED -> waitlist.complete(at);
        }
    }
}
