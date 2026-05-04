package com.seatliberator.seatliberator.reservation.domain.room;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
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

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Embedded
    private RoomOperationPolicy operationPolicy;

    private Room(
            String roomId,
            RoomOperationPolicy operationPolicy,
            Instant createdAt
    ) {
        this.roomId = Preconditions.requireNonBlank(roomId, "roomId");
        this.operationPolicy = Preconditions.requireNonNull(operationPolicy, "operationPolicy");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");
    }

    public static Room of(String roomId, RoomOperationPolicy operationPolicy, Instant createdAt) {
        return new Room(roomId, operationPolicy, createdAt);
    }

    public void updateRoomId(String roomId) {
        Preconditions.requireNonBlank(roomId, "roomId");
        this.roomId = roomId;
    }

    public void updateOperationPolicy(RoomOperationPolicy operationPolicy) {
        this.operationPolicy = Preconditions.requireNonNull(operationPolicy, "operationPolicy");
    }
}