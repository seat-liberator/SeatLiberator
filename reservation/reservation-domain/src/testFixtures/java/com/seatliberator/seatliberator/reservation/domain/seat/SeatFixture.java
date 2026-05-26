package com.seatliberator.seatliberator.reservation.domain.seat;

import com.seatliberator.seatliberator.kernel.test.FormattedStringGenerator;
import com.seatliberator.seatliberator.kernel.test.Generator;
import com.seatliberator.seatliberator.kernel.test.SequenceCounter;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class SeatFixture {
    private static final Clock CLOCK = TestClock.getFixed();

    private static final Generator<UUID> ID_GENERATOR = new UuidGenerator(new SequenceCounter());

    private static final UUID ROOM_ID = RoomFixture.nextId();

    private static final String CODE_FORMAT = "seat-%s";
    private static final Generator<String> CODE_GENERATOR = FormattedStringGenerator.of(CODE_FORMAT, new SequenceCounter());

    private static final SeatStatus STATUS = SeatStatus.ACTIVE;
    private static final Instant CREATED_AT = CLOCK.instant();

    public static Seat next() {
        var code = CODE_GENERATOR.generate();
        return Seat.of(ROOM_ID, code, STATUS, CREATED_AT);
    }

    public static UUID nextId() {
        return ID_GENERATOR.generate();
    }

    public static String nextCode() {
        return CODE_GENERATOR.generate();
    }

    public static class Builder {
        private UUID roomId = ROOM_ID;
        private String code = CODE_GENERATOR.generate();
        private SeatStatus status = STATUS;
        private Instant createdAt = CREATED_AT;

        public Builder() {
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder roomId(UUID roomId) {
            this.roomId = roomId;
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
            return Seat.of(roomId, code, status, createdAt);
        }
    }
}
