package com.seatliberator.seatliberator.reservation.domain.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "room_id", nullable = false, unique = true)
    private String roomId;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private final List<Seat> seats = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private Room(
            String roomId,
            Instant createdAt
    ) {
        if (roomId == null || roomId.isBlank()) throw new IllegalArgumentException("roomId must not be null or blank.");
        if (createdAt == null) throw new IllegalArgumentException("createdAt must not be null.");

        this.roomId = roomId;
        this.createdAt = createdAt;
    }

    public static Room of(String roomId, Instant createdAt) {
        return new Room(roomId, createdAt);
    }

    public void updateRoomId(String roomId) {
        if (roomId == null || roomId.isBlank()) throw new IllegalArgumentException("roomId must not be null or blank.");
        this.roomId = roomId;
    }

    protected void attachSeat(Seat seat) {
        if (seat == null) throw new IllegalArgumentException("seat must not be null.");
        if (seats.contains(seat)) return;
        this.seats.add(seat);
    }

    protected void detachSeat(Seat seat) {
        if (seat == null) throw new IllegalArgumentException("seat must not be null.");
        seats.remove(seat);
    }
}