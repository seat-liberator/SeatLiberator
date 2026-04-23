package com.seatliberator.seatliberator.reservation.domain.fixture;

import com.seatliberator.seatliberator.reservation.domain.persistence.Room;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;

public class RoomFixture {
    public static final String INITIAL_ROOM_ID = "study-room-1";
    private static final Instant INITIAL_CREATED_AT = fixedClock.instant();

    public static Room createRoom() {
        return Room.of(INITIAL_ROOM_ID, INITIAL_CREATED_AT);
    }

    public static Room createRoom(String roomId, Instant createdAt) {
        return Room.of(roomId, createdAt);
    }

    public static class Builder {
        private String roomId = INITIAL_ROOM_ID;
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

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Room build() {
            return Room.of(roomId, createdAt);
        }
    }
}
