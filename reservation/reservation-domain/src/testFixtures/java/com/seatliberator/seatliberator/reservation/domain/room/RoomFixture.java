package com.seatliberator.seatliberator.reservation.domain.room;

import com.seatliberator.seatliberator.kernel.test.FormattedStringGenerator;
import com.seatliberator.seatliberator.kernel.test.Generator;
import com.seatliberator.seatliberator.kernel.test.SequenceCounter;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class RoomFixture {
    private static final Clock CLOCK = TestClock.getFixed();

    private static final Generator<UUID> ID_GENERATOR = new UuidGenerator(new SequenceCounter());

    private static final String CODE_FORMAT = "study-room-%s";
    private static final Generator<String> CODE_GENERATOR = FormattedStringGenerator.of(CODE_FORMAT, new SequenceCounter());

    private static final RoomOperationPolicy OPERATION_POLICY = RoomOperationPolicyFixture.get();
    private static final Instant CREATED_AT = CLOCK.instant();

    public static Room get() {
        var code = CODE_GENERATOR.generate();
        return Room.of(code, OPERATION_POLICY, CREATED_AT);
    }

    public static UUID nextId() {
        return ID_GENERATOR.generate();
    }

    public static String nextCode() {
        return CODE_GENERATOR.generate();
    }

    public static class Builder {
        private String code = CODE_GENERATOR.generate();
        private RoomOperationPolicy operationPolicy = OPERATION_POLICY;
        private Instant createdAt = CREATED_AT;

        public Builder() {
        }

        public Builder code(String code) {
            this.code = code;
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
            return Room.of(code, operationPolicy, createdAt);
        }
    }
}
