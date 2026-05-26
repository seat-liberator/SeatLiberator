package com.seatliberator.seatliberator.reservation.domain.reservation;

import com.seatliberator.seatliberator.kernel.test.Generator;
import com.seatliberator.seatliberator.kernel.test.SequenceCounter;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;

public class ReservationFixture {
    private static final Clock CLOCK = TestClock.getFixed();

    private static final Generator<UUID> ID_GENERATOR = new UuidGenerator(new SequenceCounter());

    private static final String USER_ID = "user-1";

    private static final Instant RESERVED_AT = CLOCK.instant();
    private static final Instant TRANSITIONED_AT = CLOCK.instant().plusSeconds(5);

    public static Reservation next() {
        return Reservation.of(USER_ID, fixedClock.instant());
    }

    public static Reservation nextWithStatus(ReservationStatus status) {
        var reservation = next();
        transitionTo(reservation, status, TRANSITIONED_AT);
        return reservation;
    }

    public static UUID nextId() {
        return ID_GENERATOR.generate();
    }

    private static void transitionTo(Reservation reservation, ReservationStatus status, Instant transitionedAt) {
        switch (status) {
            case RESERVED -> {
            }
            case USED -> reservation.use(transitionedAt);
            case CANCELLED -> reservation.cancel(transitionedAt);
            case EXPIRED -> reservation.expire(transitionedAt);
        }
    }

    public static class Builder {
        private String userId = USER_ID;
        private Instant reservedAt = RESERVED_AT;
        private Instant transitionedAt = TRANSITIONED_AT;
        private ReservationStatus status = ReservationStatus.RESERVED;

        public Builder() {
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder reservedAt(Instant reservedAt) {
            this.reservedAt = reservedAt;
            return this;
        }

        public Builder status(ReservationStatus status, Instant transitionedAt) {
            this.status = status;
            this.transitionedAt = transitionedAt;
            return this;
        }

        public Reservation build() {
            var reservation = Reservation.of(userId, reservedAt);
            transitionTo(reservation, status, transitionedAt);
            return reservation;
        }
    }
}
