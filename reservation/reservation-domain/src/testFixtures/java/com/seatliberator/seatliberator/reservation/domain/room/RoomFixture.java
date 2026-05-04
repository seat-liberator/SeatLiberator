package com.seatliberator.seatliberator.reservation.domain.room;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;

public class RoomFixture {
    public static final String INITIAL_ROOM_ID = "study-room-1";
    public static final RoomOperationPolicy OPERATION_POLICY = RoomOperationPolicyFixture.get();
    private static final Instant INITIAL_CREATED_AT = fixedClock.instant();

    public static Room get() {
        return Room.of(INITIAL_ROOM_ID, OPERATION_POLICY, INITIAL_CREATED_AT);
    }

    public static Room get(String roomId, RoomOperationPolicy operationPolicy, Instant createdAt) {
        return Room.of(roomId, operationPolicy, createdAt);
    }

    public static class Builder {
        private String roomId = INITIAL_ROOM_ID;
        private RoomOperationPolicy operationPolicy = OPERATION_POLICY;
        private Instant createdAt = INITIAL_CREATED_AT;

        public Builder() {
        }

        public Builder(String roomId, Instant createdAt) {
            this.roomId = roomId;
            this.createdAt = createdAt;
        }

        public static Builder from(Builder other) {
            return new Builder(other.roomId, other.createdAt);
        }

        public Builder copy() {
            return from(this);
        }

        public Builder roomId(String roomId) {
            this.roomId = roomId;
            return this;
        }

        public Builder operationPolicy(RoomOperationPolicy operationPolicy) {
            this.operationPolicy = operationPolicy;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Room build() {
            return Room.of(roomId, operationPolicy, createdAt);
        }
    }
}
