package com.seatliberator.seatliberator.reservation.domain.waitlist;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistFixture.*;

public class WaitlistFixtureBuilder {
    private String userId = INITIAL_USER_ID;

    private List<UUID> slotIds = INITIAL_SLOT_IDS;

    private LocalDate occupancyDate = INITIAL_OCCUPANCY_DATE;

    private Instant requestedAt = fixedClock.instant();

    private WaitlistBehavior behavior = WaitlistBehavior.NOTIFY_ONLY;

    public WaitlistFixtureBuilder() {
    }

    public WaitlistFixtureBuilder(
            String userId,
            List<UUID> slotIds,
            LocalDate occupancyDate,
            Instant requestedAt,
            WaitlistBehavior behavior
    ) {
        this.userId = userId;
        this.slotIds = slotIds;
        this.occupancyDate = occupancyDate;
        this.requestedAt = requestedAt;
        this.behavior = behavior;
    }

    public WaitlistFixtureBuilder copy() {
        return new WaitlistFixtureBuilder(
                userId,
                List.copyOf(slotIds),
                occupancyDate,
                requestedAt,
                behavior
        );
    }

    public WaitlistFixtureBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public WaitlistFixtureBuilder slotIds(List<UUID> slotIds) {
        this.slotIds = slotIds;
        return this;
    }

    public WaitlistFixtureBuilder occupancyDate(LocalDate occupancyDate) {
        this.occupancyDate = occupancyDate;
        return this;
    }

    public WaitlistFixtureBuilder requestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
        return this;
    }

    public WaitlistFixtureBuilder behavior(WaitlistBehavior behavior) {
        this.behavior = behavior;
        return this;
    }

    public Waitlist build() {
        return Waitlist.of(userId, slotIds, occupancyDate, behavior, requestedAt);
    }
}
