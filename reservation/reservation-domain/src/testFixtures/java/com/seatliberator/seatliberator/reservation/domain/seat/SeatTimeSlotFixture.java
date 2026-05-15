package com.seatliberator.seatliberator.reservation.domain.seat;

import com.seatliberator.seatliberator.reservation.domain.shared.TestSupport;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRangeFixture;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture.createSeat;

public class SeatTimeSlotFixture {
    public static final Seat SEAT = createSeat();
    public static final DailyNanoRange SLOT_RANGE = DailyNanoRangeFixture.get();
    public static final SeatTimeSlotStatus SLOT_STATUS = SeatTimeSlotStatus.ACTIVE;
    public static final Instant CREATED_AT = TestSupport.fixedClock.instant();

    public static SeatTimeSlot get() {
        return SeatTimeSlot.of(SEAT, SLOT_RANGE, SLOT_STATUS, CREATED_AT);
    }

    public static class Builder {
        private Seat seat = SEAT;
        private DailyNanoRange slotRange = SLOT_RANGE;
        private SeatTimeSlotStatus slotStatus = SLOT_STATUS;
        private Instant createdAt = CREATED_AT;

        public Builder seat(Seat seat) {
            this.seat = seat;
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
            return SeatTimeSlot.of(seat, slotRange, slotStatus, createdAt);
        }
    }
}
