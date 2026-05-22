package com.seatliberator.seatliberator.reservation.domain.reservation;

import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocatorFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRangeFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleInstantRange;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;

public class ReservationFixture {
    public static final String INITIAL_USER_ID = "user-1";
    public static final String INITIAL_ROOM_ID = "room-1";
    public static final String INITIAL_SEAT_ID = "seat-1";
    public static final Duration INITIAL_DURATION = Duration.ofMinutes(30);

    public static Reservation createReservation() {
        return Reservation.of(INITIAL_USER_ID, fixedClock.instant());
    }

    public static Reservation createReservation(ReservationStatus status) {
        var startTime = fixedClock.instant();
        var endTime = startTime.plus(INITIAL_DURATION);
        return createReservation(startTime, endTime, status);
    }

    public static Reservation createReservation(Instant startTime, Instant endTime, ReservationStatus status) {
        var reservation = Reservation.of(INITIAL_USER_ID, startTime);
        transitionTo(reservation, status, endTime);
        return reservation;
    }

    private static void transitionTo(Reservation reservation, ReservationStatus status, Instant at) {
        switch (status) {
            case RESERVED -> {
            }
            case USED -> reservation.use(at);
            case CANCELLED -> reservation.cancel(at);
            case EXPIRED -> reservation.expire(at);
        }
    }

    public static class Builder {
        private String userId = INITIAL_USER_ID;
        private SeatLocator locator = SeatLocatorFixture.get();
        private InstantRange range = InstantRangeFixture.get();
        private ReservationStatus status = ReservationStatus.RESERVED;

        public Builder() {
        }

        public Builder(String userId, SeatLocator locator, InstantRange range, ReservationStatus status) {
            this.userId = userId;
            this.locator = locator;
            this.range = range;
            this.status = status;
        }

        public static Builder from(Builder other) {
            return new Builder(
                    other.userId,
                    SimpleSeatLocator.from(other.locator),
                    SimpleInstantRange.from(other.range),
                    other.status
            );
        }

        public Builder copy() {
            return from(this);
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder seatId(String seatId) {
            this.locator = SimpleSeatLocator.of(locator.roomId(), seatId);
            return this;
        }

        public Builder roomId(String roomId) {
            this.locator = SimpleSeatLocator.of(roomId, locator.seatId());
            return this;
        }

        public Builder locator(SeatLocator locator) {
            this.locator = SimpleSeatLocator.from(locator);
            return this;
        }

        public Builder startAt(Instant startAt) {
            this.range = SimpleInstantRange.of(startAt, range.endAt());
            return this;
        }

        public Builder endAt(Instant endAt) {
            this.range = SimpleInstantRange.of(range.startAt(), endAt);
            return this;
        }

        public Builder range(InstantRange range) {
            this.range = SimpleInstantRange.from(range);
            return this;
        }

        public Builder status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public Reservation build() {
            var reservation = Reservation.of(userId, range.startAt());
            transitionTo(reservation, status, range.endAt());
            return reservation;
        }
    }
}
