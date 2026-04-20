package com.seatliberator.seatliberator.reservation.domain.fixture;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;

public class SeatFixture {
    private static final Instant INITIAL_CREATED_AT = fixedClock.instant();

    public static Seat create(SeatLocator locator, Instant createdAt) {
        return Seat.create(locator, createdAt);
    }

    public static Seat create(SeatLocator locator) {
        return create(locator, INITIAL_CREATED_AT);
    }

    public static Seat create() {
        return create(createLocator(), INITIAL_CREATED_AT);
    }

    public static class Builder {
        private SeatLocator locator = createLocator();
        private Instant createdAt = INITIAL_CREATED_AT;

        public Builder() {
        }

        public Builder(SeatLocator locator, Instant createdAt) {
            this.locator = locator;
            this.createdAt = createdAt;
        }

        public static Builder from(Builder other) {
            return new Builder(SimpleSeatLocator.from(other.locator), other.createdAt);
        }

        public Builder copy() {
            return from(this);
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

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Seat build() {
            return create(locator, createdAt);
        }
    }
}
