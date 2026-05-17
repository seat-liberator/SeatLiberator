package com.seatliberator.seatliberator.reservation.domain.reservation;

import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotFixture;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;

public class SeatOccupancyFixture {
    public static final SeatTimeSlot SEAT_TIME_SLOT = SeatTimeSlotFixture.get();
    public static final Reservation RESERVATION = ReservationFixture.createReservation();
    public static final UUID SEAT_TIME_SLOT_ID = new UUID(0L, 1L);
    public static final UUID RESERVATION_ID = new UUID(0L, 2L);
    public static final LocalDate OCCUPANCY_DATE = LocalDate.now(fixedClock);
    public static final Instant CREATED_AT = fixedClock.instant();

    public static SeatOccupancy get() {
        return SeatOccupancy.of(SEAT_TIME_SLOT_ID, RESERVATION_ID, OCCUPANCY_DATE, CREATED_AT);
    }

    public static class Builder {
        private UUID seatTimeSlotId = SEAT_TIME_SLOT_ID;
        private UUID reservationId = RESERVATION_ID;
        private LocalDate occupancyDate = OCCUPANCY_DATE;
        private Instant createdAt = CREATED_AT;

        public Builder seatTimeSlot(SeatTimeSlot seatTimeSlot) {
            this.seatTimeSlotId = seatTimeSlot.getId() == null ? SEAT_TIME_SLOT_ID : seatTimeSlot.getId();
            return this;
        }

        public Builder seatTimeSlotId(UUID seatTimeSlotId) {
            this.seatTimeSlotId = seatTimeSlotId;
            return this;
        }

        public Builder reservation(Reservation reservation) {
            this.reservationId = reservation.getId() == null ? RESERVATION_ID : reservation.getId();
            return this;
        }

        public Builder reservationId(UUID reservationId) {
            this.reservationId = reservationId;
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

        public SeatOccupancy build() {
            return SeatOccupancy.of(seatTimeSlotId, reservationId, occupancyDate, createdAt);
        }
    }
}
