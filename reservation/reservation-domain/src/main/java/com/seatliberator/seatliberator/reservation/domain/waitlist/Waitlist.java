package com.seatliberator.seatliberator.reservation.domain.waitlist;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "waitlist")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Waitlist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @ElementCollection
    @CollectionTable(
            name = "waitlist_slot",
            joinColumns = @JoinColumn(name = "waitlist_slot_id")
    )
    @Column(name = "slot_id", nullable = false)
    private List<UUID> slotIds = new ArrayList<>();

    @Column(name = "occupancy_date", nullable = false)
    private LocalDate occupancyDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "behavior", nullable = false)
    private WaitlistBehavior behavior;

    @Embedded
    private WaitlistState state;

    private Waitlist(
            String userId,
            List<UUID> slotIds,
            LocalDate occupancyDate,
            WaitlistBehavior behavior,
            WaitlistState state
    ) {
        this.userId = Preconditions.requireNonBlank(userId, "userId");
        this.slotIds = List.copyOf(Preconditions.requireNonNull(slotIds, "slotIds"));
        this.occupancyDate = Preconditions.requireNonNull(occupancyDate, "occupancyDate");
        this.behavior = Preconditions.requireNonNull(behavior, "behavior");
        this.state = Preconditions.requireNonNull(state, "state");
    }

    public static Waitlist of(String userId, List<UUID> slotIds, LocalDate occupancyDate, WaitlistBehavior behavior, WaitlistState state) {
        return new Waitlist(userId, slotIds, occupancyDate, behavior, state);
    }

    public static Waitlist of(String userId, List<UUID> slotIds, LocalDate occupancyDate, WaitlistBehavior behavior, Instant requestedAt) {
        return new Waitlist(userId, slotIds, occupancyDate, behavior, WaitlistState.requestedAt(requestedAt));
    }

    public static Waitlist notifyOnly(
            String userId,
            List<UUID> slotIds,
            LocalDate occupancyDate,
            Instant requestedAt
    ) {
        return of(userId, slotIds, occupancyDate, WaitlistBehavior.NOTIFY_ONLY, WaitlistState.requestedAt(requestedAt));
    }

    public static Waitlist autoClaim(
            String userId,
            List<UUID> slotIds,
            LocalDate occupancyDate,
            Instant requestedAt
    ) {
        return of(userId, slotIds, occupancyDate, WaitlistBehavior.AUTO_CLAIM, WaitlistState.requestedAt(requestedAt));
    }

    public void cancel(Instant cancelledAt) {
        this.state.cancel(cancelledAt);
    }

    public void expire(Instant expiredAt) {
        this.state.expire(expiredAt);
    }

    public void fail(Instant failedAt) {
        this.state.fail(failedAt);
    }

    public void complete(Instant completedAt) {
        switch (behavior) {
            case NOTIFY_ONLY -> state.completeAsNotified(completedAt);
            case AUTO_CLAIM -> state.completeAtClaimed(completedAt);
        }
    }
}
