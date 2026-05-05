package com.seatliberator.seatliberator.reservation.domain.seat;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeWindow;
import com.seatliberator.seatliberator.reservation.domain.shared.EmbeddableDailyTimeWindow;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "seat_time_slot",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seat_time_slot_seat_id_and_slot_start_at_slot_and_end_at",
                        columnNames = {"seat_id", "slot_start_at", "slot_end_at"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startAt", column = @Column(name = "slot_start_at", nullable = false)),
            @AttributeOverride(name = "endAt", column = @Column(name = "slot_end_at", nullable = false))
    })
    private EmbeddableDailyTimeWindow slotRange;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_status", nullable = false)
    private SeatTimeSlotStatus slotStatus;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_activated_at")
    private Instant lastActivatedAt;

    @Column(name = "last_inactivated_at")
    private Instant lastInactivatedAt;

    private SeatTimeSlot(
            Seat seat,
            EmbeddableDailyTimeWindow slotRange,
            SeatTimeSlotStatus slotStatus,
            Instant createdAt
    ) {
        this.seat = Preconditions.requireNonNull(seat, "seat");
        this.slotRange = Preconditions.requireNonNull(slotRange, "slotRange");
        this.slotStatus = Preconditions.requireNonNull(slotStatus, "slotStatus");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");

        switch (slotStatus) {
            case ACTIVE -> this.lastActivatedAt = createdAt;
            case INACTIVE -> this.lastInactivatedAt = createdAt;
        }
    }

    public static SeatTimeSlot of(
            Seat seat,
            DailyTimeWindow slotRange,
            SeatTimeSlotStatus slotStatus,
            Instant createdAt
    ) {
        Preconditions.requireNonNull(slotRange, "slotRange");
        return new SeatTimeSlot(
                seat,
                EmbeddableDailyTimeWindow.from(slotRange),
                slotStatus,
                createdAt
        );
    }

    public void updateSlotRange(DailyTimeWindow slotRange) {
        Preconditions.requireNonNull(slotRange, "slotRange");
        this.slotRange.apply(slotRange);
    }

    public void active(Instant activatedAt) {
        Preconditions.requireNonNull(activatedAt, "activatedAt");

        ensureDifferentStatus(SeatTimeSlotStatus.ACTIVE);
        ensureNotBefore(activatedAt, "activatedAt", createdAt, "createdAt");
        ensureNotBefore(activatedAt, "activatedAt", lastInactivatedAt, "lastInactivatedAt");

        this.lastActivatedAt = activatedAt;
        this.slotStatus = SeatTimeSlotStatus.ACTIVE;
    }

    public void inactive(Instant inactivatedAt) {
        Preconditions.requireNonNull(inactivatedAt, "inactivatedAt");

        ensureDifferentStatus(SeatTimeSlotStatus.INACTIVE);
        ensureNotBefore(inactivatedAt, "inactivatedAt", createdAt, "createdAt");
        ensureNotBefore(inactivatedAt, "inactivatedAt", lastActivatedAt, "lastActivatedAt");

        this.lastInactivatedAt = inactivatedAt;
        this.slotStatus = SeatTimeSlotStatus.INACTIVE;
    }

    private void ensureNotBefore(Instant at, String fieldName, Instant ref, String refFieldName) {
        if (at.isBefore(ref)) throw new IllegalArgumentException(String.format(
                "%s can not be earlier than %s.",
                fieldName,
                refFieldName
        ));
    }

    private void ensureDifferentStatus(SeatTimeSlotStatus slotStatus) {
        if (this.slotStatus == slotStatus) {
            throw new IllegalStateException(String.format(
                    "Can not transition to %s from %s",
                    slotStatus.name().toLowerCase(),
                    this.slotStatus.name().toLowerCase()
            ));
        }
    }
}
