package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "room_id")
    private List<Seat> seats = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private Room(
            String roomId,
            List<Seat> seats,
            Instant createdAt
    ) {
        if (roomId == null || roomId.isBlank()) throw new IllegalArgumentException("roomId must not be null or blank.");
        if (createdAt == null) throw new IllegalArgumentException("createdAt must not be null or blank.");

        this.roomId = roomId;
        this.seats = new ArrayList<>(seats);
        this.createdAt = createdAt;

        for (var seat : seats) {
            ensureSeatBelongToRoom(seat);
        }
    }

    public static Room of(String roomId, Instant createdAt) {
        return new Room(roomId, List.of(), createdAt);
    }

    public static Room of(String roomId, List<Seat> seats, Instant createdAt) {
        return new Room(roomId, seats, createdAt);
    }

    public void addSeat(Seat seat) {
        ensureSeatBelongToRoom(seat);
        this.seats.add(seat);
    }

    public SeatLocator locatorOf(Seat seat) {
        ensureSeatBelongToRoom(seat);
        return SimpleSeatLocator.of(roomId, seat.getLocator().seatId());
    }

    private void ensureSeatBelongToRoom(Seat seat) {
        var locator = seat.getLocator();
        if (!locator.roomId().equals(roomId)) {
            throw new IllegalArgumentException(String.format(
                    "seat must belong to room. seatId=%s, roomId expected=%s actual=%s",
                    locator.seatId(),
                    roomId,
                    locator.roomId()
            ));
        }
    }
}