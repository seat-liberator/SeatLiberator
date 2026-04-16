package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.EmbeddableSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SeatStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"room_id", "seat_id"})
        }
)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private EmbeddableSeatLocator locator;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SeatStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_activated_at", nullable = false)
    private Instant lastActivatedAt;

    @Column(name = "last_inactivated_at")
    private Instant lastInactivatedAt;

    private Seat(
            EmbeddableSeatLocator locator,
            SeatStatus status,
            Instant createdAt
    ) {
        if (locator == null) throw new IllegalArgumentException("locator must not be null.");
        if (status == null) throw new IllegalArgumentException("status must not be null.");
        if (createdAt == null) throw new IllegalArgumentException("createdAt must not be null.");
        this.locator = locator;
        this.status = status;
        this.createdAt = createdAt;
        this.lastActivatedAt = createdAt;
    }

    public static Seat create(String roomId, String seatId, Instant createdAt) {
        return create(EmbeddableSeatLocator.from(roomId, seatId), createdAt);
    }

    public static Seat create(SeatLocator locator, Instant createdAt) {
        if (locator == null) throw new IllegalArgumentException("locator must not be null.");
        return create(EmbeddableSeatLocator.of(locator), createdAt);
    }

    private static Seat create(EmbeddableSeatLocator locator, Instant createdAt) {
        return new Seat(locator, SeatStatus.ACTIVE, createdAt);
    }

    public void update(SeatLocator locator) {
        if (locator == null) throw new IllegalArgumentException("locator must not be null.");
        this.locator.setLocate(locator.roomId(), locator.seatId());
    }

    public void update(String roomId, String seatId) {
        this.locator.setLocate(roomId, seatId);
    }

    public void active(Instant activatedAt) {
        if (activatedAt == null) throw new IllegalArgumentException("activatedAt must not be null.");

        ensureDifferentStatus(SeatStatus.ACTIVE);
        ensureNotBefore(activatedAt, "activatedAt", createdAt, "createdAt");
        ensureNotBefore(activatedAt, "activatedAt", lastInactivatedAt, "lastInactivatedAt");

        this.lastActivatedAt = activatedAt;
        this.status = SeatStatus.ACTIVE;
    }

    public void inactive(Instant inactivatedAt) {
        if (inactivatedAt == null) throw new IllegalArgumentException("inactivatedAt must not be null.");

        ensureDifferentStatus(SeatStatus.INACTIVE);
        ensureNotBefore(inactivatedAt, "inactivatedAt", createdAt, "createdAt");
        ensureNotBefore(inactivatedAt, "inactivatedAt", lastActivatedAt, "lastActivatedAt");

        this.lastInactivatedAt = inactivatedAt;
        this.status = SeatStatus.INACTIVE;
    }

    private void ensureNotBefore(Instant at, String fieldName, Instant ref, String refFieldName) {
        if (at.isBefore(ref)) throw new IllegalArgumentException(String.format(
                "%s can not be earlier than %s.",
                fieldName,
                refFieldName
        ));
    }

    private void ensureNotBeforeCreatedAt(Instant at, String fieldName) {
        if (at.isBefore(createdAt)) throw new IllegalArgumentException(String.format(
                "%s can not be earlier than createdAt.",
                fieldName
        ));
    }

    private void ensureDifferentStatus(SeatStatus status) {
        if (this.status == status) {
            throw new IllegalStateException(String.format(
                    "Can not transition to %s from %s",
                    status.name().toLowerCase(),
                    this.status.name().toLowerCase()
            ));
        }
    }
}
