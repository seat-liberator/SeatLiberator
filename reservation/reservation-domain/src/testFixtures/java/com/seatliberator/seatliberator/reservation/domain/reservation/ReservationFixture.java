package com.seatliberator.seatliberator.reservation.domain.reservation;

import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.shared.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.shared.TimeRangeFixture.createRange;
import static org.assertj.core.api.Fail.fail;

public class ReservationFixture {
    public static final String INITIAL_USER_ID = "user-1";
    public static final String INITIAL_ROOM_ID = "room-1";
    public static final String INITIAL_SEAT_ID = "seat-1";
    public static final Duration INITIAL_DURATION = Duration.ofMinutes(30);

    public static Reservation createReservation() {
        var startTime = fixedClock.instant();
        var endTime = startTime.plus(INITIAL_DURATION);
        return createReservation(startTime, endTime, ReservationStatus.RESERVED);
    }

    public static Reservation createReservation(ReservationStatus status) {
        var startTime = fixedClock.instant();
        var endTime = startTime.plus(INITIAL_DURATION);
        return createReservation(startTime, endTime, status);
    }

    public static Reservation createReservation(Instant startTime, Instant endTime, ReservationStatus status) {
        return Reservation.create(INITIAL_USER_ID, INITIAL_ROOM_ID, INITIAL_SEAT_ID, startTime, endTime, status);
    }

    public static void stubReservationId(Reservation reservation, UUID id) {
        try {
            var idField = Reservation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(reservation, id);
        } catch (ReflectiveOperationException e) {
            fail("테스트용 ID 설정 실패");
        }
    }

    public static class Builder {
        private String userId = INITIAL_USER_ID;
        private SeatLocator locator = createLocator();
        private TimeRange range = createRange();
        private ReservationStatus status = ReservationStatus.RESERVED;

        public Builder() {
        }

        public Builder(String userId, SeatLocator locator, TimeRange range, ReservationStatus status) {
            this.userId = userId;
            this.locator = locator;
            this.range = range;
            this.status = status;
        }

        public static Builder from(Builder other) {
            return new Builder(
                    other.userId,
                    SimpleSeatLocator.from(other.locator),
                    SimpleTimeRange.from(other.range),
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
            this.range = SimpleTimeRange.of(startAt, range.endAt());
            return this;
        }

        public Builder endAt(Instant endAt) {
            this.range = SimpleTimeRange.of(range.startAt(), endAt);
            return this;
        }

        public Builder range(TimeRange range) {
            this.range = SimpleTimeRange.from(range);
            return this;
        }

        public Builder status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public Reservation build() {
            return Reservation.create(userId, locator.roomId(), locator.seatId(), range.startAt(), range.endAt(), status);
        }
    }
}
