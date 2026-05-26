package com.seatliberator.seatliberator.reservation.domain.seat;

import com.seatliberator.seatliberator.kernel.test.Generator;
import com.seatliberator.seatliberator.kernel.test.SequenceCounter;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRangeFixture;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class SeatTimeSlotFixture {
    private static final Clock CLOCK = TestClock.getFixed();

    private static final Generator<UUID> ID_GENERATOR = new UuidGenerator(new SequenceCounter());

    private static final UUID SEAT_ID = SeatFixture.nextId();

    private static final DailyNanoRange SLOT_RANGE = DailyNanoRangeFixture.get();
    private static final SeatTimeSlotStatus STATUS = SeatTimeSlotStatus.ACTIVE;

    private static final Instant CREATED_AT = CLOCK.instant();

    public static SeatTimeSlot get() {
        return SeatTimeSlot.of(SEAT_ID, SLOT_RANGE, STATUS, CREATED_AT);
    }

    public static UUID nextId() {
        return ID_GENERATOR.generate();
    }

    public static class Builder {
        private UUID seatId = SEAT_ID;
        private DailyNanoRange slotRange = SLOT_RANGE;
        private SeatTimeSlotStatus slotStatus = STATUS;
        private Instant createdAt = CREATED_AT;

        public Builder seatId(UUID seatId) {
            this.seatId = seatId;
            return this;
        }

        public Builder slotRange(DailyNanoRange slotRange) {
            this.slotRange = slotRange;
            return this;
        }

        public Builder slotStatus(SeatTimeSlotStatus slotStatus) {
            this.slotStatus = slotStatus;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SeatTimeSlot build() {
            return SeatTimeSlot.of(seatId, slotRange, slotStatus, createdAt);
        }
    }
}
