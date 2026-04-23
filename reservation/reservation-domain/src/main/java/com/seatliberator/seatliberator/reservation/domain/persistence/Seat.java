package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SeatStatus;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
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
                @UniqueConstraint(columnNames = {"room_pk", "seat_id"})
        }
)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_pk", nullable = false)
    private Room room;

    @Column(name = "seat_id", nullable = false)
    private String seatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SeatStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_activated_at")
    private Instant lastActivatedAt;

    @Column(name = "last_inactivated_at")
    private Instant lastInactivatedAt;

    private Seat(
            Room room,
            String seatId,
            SeatStatus status,
            Instant createdAt
    ) {
        if (room == null) throw new IllegalArgumentException("room must not be null.");
        if (seatId == null || seatId.isBlank()) throw new IllegalArgumentException("seatId must not be null or blank.");
        if (status == null) throw new IllegalArgumentException("status must not be null.");
        if (createdAt == null) throw new IllegalArgumentException("createdAt must not be null.");

        this.room = room;
        this.seatId = seatId;
        this.status = status;
        this.createdAt = createdAt;

        room.attachSeat(this);

        switch (status) {
            case ACTIVE -> this.lastActivatedAt = createdAt;
            case INACTIVE -> this.lastInactivatedAt = createdAt;
        }
    }

    public static Seat of(Room room, String seatId, Instant createdAt) {
        return of(room, seatId, SeatStatus.ACTIVE, createdAt);
    }

    public static Seat of(Room room, String seatId, SeatStatus status, Instant createdAt) {
        return new Seat(room, seatId, status, createdAt);
    }

    public SeatLocator getLocator() {
        return SimpleSeatLocator.of(room.getRoomId(), seatId);
    }

    public void updateSeatId(String seatId) {
        if (seatId == null || seatId.isBlank()) throw new IllegalArgumentException("seatId must not be null or blank.");
        this.seatId = seatId;
    }

    public void updateRoom(Room room) {
        if (room == null) throw new IllegalArgumentException("room must not be null.");
        if (this.room.equals(room)) return;

        var oldRoom = this.room;
        oldRoom.detachSeat(this);
        this.room = room;
        room.attachSeat(this);
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
