package com.seatliberator.seatliberator.reservation.domain.seat;

import com.seatliberator.seatliberator.reservation.domain.room.Room;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.room.RoomFixture.createRoom;
import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;

public class SeatFixture {
    public static final Room INITIAL_ROOM = createRoom();
    public static final String INITIAL_SEAT_ID = "seat-a";
    public static final SeatStatus INITIAL_STATUS = SeatStatus.ACTIVE;
    public static final Instant INITIAL_CREATED_AT = fixedClock.instant();

    public static Seat createSeat() {
        return create(INITIAL_ROOM, INITIAL_SEAT_ID, INITIAL_CREATED_AT);
    }

    public static Seat create(Room room, String seatId, Instant createdAt) {
        return Seat.of(room, seatId, createdAt);
    }

    public static class Builder {
        private Room room = INITIAL_ROOM;
        private String seatId = INITIAL_SEAT_ID;
        private SeatStatus status = INITIAL_STATUS;
        private Instant createdAt = INITIAL_CREATED_AT;

        public Builder() {
        }

        public Builder(Room room, String seatId, SeatStatus status, Instant createdAt) {
            this.room = room;
            this.seatId = seatId;
            this.status = status;
            this.createdAt = createdAt;
        }

        public static Builder from(Builder other) {
            return new Builder(other.room, other.seatId, other.status, other.createdAt);
        }

        public Builder copy() {
            return from(this);
        }

        public Builder seatId(String seatId) {
            this.seatId = seatId;
            return this;
        }

        public Builder room(Room room) {
            this.room = room;
            return this;
        }

        public Builder status(SeatStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Seat build() {
            return Seat.of(room, seatId, status, createdAt);
        }
    }
}
