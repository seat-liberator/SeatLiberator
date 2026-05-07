package com.seatliberator.seatliberator.reservation.domain.reservation;

import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotFixture;

import java.time.Instant;
import java.time.LocalDate;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;

public class SeatTimeSlotOccupancyFixture {
    public static final SeatTimeSlot SEAT_TIME_SLOT = SeatTimeSlotFixture.get();
    public static final Reservation RESERVATION = ReservationFixture.createReservation();
    public static final LocalDate OCCUPANCY_DATE = LocalDate.now(fixedClock);
    public static final Instant CREATED_AT = fixedClock.instant();

    public static SeatTimeSlotOccupancy get() {
        return SeatTimeSlotOccupancy.of(SEAT_TIME_SLOT, RESERVATION, OCCUPANCY_DATE, CREATED_AT);
    }

    public static class Builder {
        private SeatTimeSlot seatTimeSlot = SEAT_TIME_SLOT;
        private Reservation reservation = RESERVATION;
        private LocalDate occupancyDate = OCCUPANCY_DATE;
        private Instant createdAt = CREATED_AT;

        public Builder seatTimeSlot(SeatTimeSlot seatTimeSlot) {
            this.seatTimeSlot = seatTimeSlot;
            return this;
        }

        public Builder reservation(Reservation reservation) {
            this.reservation = reservation;
            return this;
        }

        public Builder occupancyDate(LocalDate occupancyDate) {
            this.occupancyDate = occupancyDate;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SeatTimeSlotOccupancy build() {
            return SeatTimeSlotOccupancy.of(seatTimeSlot, reservation, occupancyDate, createdAt);
        }
    }
}
