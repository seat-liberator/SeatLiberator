package com.seatliberator.seatliberator.reservation.domain.seat;

import com.seatliberator.seatliberator.kernel.test.FormattedStringGenerator;
import com.seatliberator.seatliberator.kernel.test.Generator;
import com.seatliberator.seatliberator.kernel.test.SequenceCounter;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class SeatFixture {
    private static final Clock CLOCK = TestClock.getFixed();

    private static final Generator<UUID> ID_GENERATOR = new UuidGenerator(new SequenceCounter());

    private static final Room ROOM = RoomFixture.get();

    private static final String CODE_FORMAT = "seat-%s";
    private static final Generator<String> CODE_GENERATOR = FormattedStringGenerator.of(CODE_FORMAT, new SequenceCounter());

    private static final SeatStatus STATUS = SeatStatus.ACTIVE;
    private static final Instant CREATED_AT = CLOCK.instant();

    public static Seat next() {
        var code = CODE_GENERATOR.generate();
        return Seat.of(ROOM, code, STATUS, CREATED_AT);
    }

    public static Seat nextWithRoom(Room room) {
        var code = CODE_GENERATOR.generate();
        return Seat.of(room, code, STATUS, CREATED_AT);
    }

    public static UUID nextId() {
        return ID_GENERATOR.generate();
    }

    public static String nextCode() {
        return CODE_GENERATOR.generate();
    }

    public static class Builder {
        private Room room = ROOM;
        private String code = CODE_GENERATOR.generate();
        private SeatStatus status = STATUS;
        private Instant createdAt = CREATED_AT;

        public Builder() {
        }

        public Builder code(String code) {
            this.code = code;
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
            return Seat.of(room, code, status, createdAt);
        }
    }
}
