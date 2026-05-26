package com.seatliberator.seatliberator.reservation.domain.room;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "room",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_room_code",
                columnNames = {"code"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Embedded
    private RoomOperationPolicy operationPolicy;

    private Room(
            String code,
            RoomOperationPolicy operationPolicy,
            Instant createdAt
    ) {
        this.code = Preconditions.requireNonBlank(code, "code");
        this.operationPolicy = Preconditions.requireNonNull(operationPolicy, "operationPolicy");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");
    }

    public static Room of(String code, RoomOperationPolicy operationPolicy, Instant createdAt) {
        return new Room(code, operationPolicy, createdAt);
    }

    public void updateCode(String code) {
        Preconditions.requireNonBlank(code, "code");
        this.code = code;
    }

    public void updateOperationPolicy(RoomOperationPolicy operationPolicy) {
        this.operationPolicy = Preconditions.requireNonNull(operationPolicy, "operationPolicy");
    }
}