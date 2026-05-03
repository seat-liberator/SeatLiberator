package com.seatliberator.seatliberator.reservation.domain.seat;

import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture.createSeat;

public class SeatTimeSlotFixture {
    public static final Seat SEAT = createSeat();
    public static final TimeRange SLOT_RANGE = createRange(
            TestSupport.fixedClock.instant(),
            TestSupport.fixedClock.instant().plus(30, ChronoUnit.MINUTES)
    );
    public static final SeatTimeSlotStatus SLOT_STATUS = SeatTimeSlotStatus.ACTIVE;
    public static final Instant CREATED_AT = TestSupport.fixedClock.instant();

    public static SeatTimeSlot get() {
        return SeatTimeSlot.of(SEAT, SLOT_RANGE, SLOT_STATUS, CREATED_AT);
    }

    public static class Builder {
        private Seat seat = SEAT;
        private TimeRange slotRange = SLOT_RANGE;
        private SeatTimeSlotStatus slotStatus = SLOT_STATUS;
        private Instant createdAt = CREATED_AT;

        public Builder seat(Seat seat) {
            this.seat = seat;
            return this;
        }

        public Builder slotRange(TimeRange slotRange) {
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
            return SeatTimeSlot.of(seat, slotRange, slotStatus, createdAt);
        }
    }
}
