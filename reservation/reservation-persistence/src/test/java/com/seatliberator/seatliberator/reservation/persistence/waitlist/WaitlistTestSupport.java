package com.seatliberator.seatliberator.reservation.persistence.waitlist;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistBehavior;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class WaitlistTestSupport {
    public static final Clock CLOCK = TestClock.getFixed();
    public static final Instant NOW = CLOCK.instant();
    public static final Instant REQUESTED_AT = NOW;
    public static final Instant TRANSITIONED_AT = REQUESTED_AT.plusSeconds(60);
    public static final UUID UNKNOWN_WAITLIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000601");
    public static final UUID SLOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID OTHER_SLOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    public static final String USER_ID = "user-1";
    public static final String OTHER_USER_ID = "user-2";
    public static final LocalDate OCCUPANCY_DATE = LocalDate.now(CLOCK);

    private WaitlistTestSupport() {
    }

    public static Waitlist waitlist() {
        return waitlist(USER_ID, List.of(SLOT_ID), OCCUPANCY_DATE);
    }

    public static Waitlist waitlist(String userId, List<UUID> slotIds, LocalDate occupancyDate) {
        return waitlist(userId, slotIds, occupancyDate, WaitlistBehavior.AUTO_CLAIM);
    }

    public static Waitlist waitlist(String userId, List<UUID> slotIds, LocalDate occupancyDate, WaitlistBehavior behavior) {
        return Waitlist.of(userId, slotIds, occupancyDate, behavior, REQUESTED_AT);
    }

    public static void assertSameWaitlist(Waitlist actual, Waitlist expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
        assertThat(actual.getSlotIds()).containsExactlyElementsOf(expected.getSlotIds());
        assertThat(actual.getOccupancyDate()).isEqualTo(expected.getOccupancyDate());
        assertThat(actual.getBehavior()).isEqualTo(expected.getBehavior());
        assertThat(actual.getState().getStatus()).isEqualTo(expected.getState().getStatus());
        assertThat(actual.getState().getResolution()).isEqualTo(expected.getState().getResolution());
        assertThat(actual.getState().getRequestedAt()).isEqualTo(expected.getState().getRequestedAt());
        assertThat(actual.getState().getCancelledAt()).isEqualTo(expected.getState().getCancelledAt());
        assertThat(actual.getState().getExpiredAt()).isEqualTo(expected.getState().getExpiredAt());
        assertThat(actual.getState().getFailedAt()).isEqualTo(expected.getState().getFailedAt());
        assertThat(actual.getState().getCompletedAt()).isEqualTo(expected.getState().getCompletedAt());
    }
}
