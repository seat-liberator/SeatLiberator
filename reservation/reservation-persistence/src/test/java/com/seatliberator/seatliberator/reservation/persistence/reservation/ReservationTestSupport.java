package com.seatliberator.seatliberator.reservation.persistence.reservation;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleInstantRange;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationTestSupport {
    public static final Clock CLOCK = TestClock.getFixed();
    public static final Instant NOW = CLOCK.instant();
    public static final UUID UNKNOWN_RESERVATION_ID = UUID.fromString("00000000-0000-0000-0000-000000000301");
    public static final String USER_ID = "user-1";
    public static final String OTHER_USER_ID = "user-2";
    public static final Duration RESERVATION_OFFSET = Duration.ofHours(1);
    public static final Duration STATE_RANGE_PADDING = Duration.ofMinutes(1);

    private ReservationTestSupport() {
    }

    public static Reservation reservation() {
        return reservation(USER_ID, ReservationStatus.RESERVED);
    }

    public static Reservation reservation(String userId) {
        return reservation(userId, ReservationStatus.RESERVED);
    }

    public static Reservation reservation(ReservationStatus status) {
        return reservation(USER_ID, status);
    }

    public static Reservation reservation(String userId, ReservationStatus status) {
        var reservation = Reservation.of(userId, reservationStartAt());

        switch (status) {
            case RESERVED -> {
            }
            case USED -> reservation.use(reservationStartAt().plus(Duration.ofMinutes(10)));
            case CANCELLED -> reservation.cancel(reservationStartAt().minus(Duration.ofMinutes(10)));
            case EXPIRED -> reservation.expire(reservationStartAt().minus(Duration.ofMinutes(10)));
        }

        return reservation;
    }

    public static Reservation reservationAt(Instant reservedAt) {
        return Reservation.of(USER_ID, reservedAt);
    }

    public static Instant reservationStartAt() {
        return NOW.plus(RESERVATION_OFFSET);
    }

    public static InstantRange stateRange(Reservation reservation) {
        var reservedAt = reservation.getState().getReservedAt();
        return range(
                reservedAt.minus(STATE_RANGE_PADDING),
                reservedAt.plus(STATE_RANGE_PADDING)
        );
    }

    public static InstantRange range(Instant startAt, Instant endAt) {
        return SimpleInstantRange.of(startAt, endAt);
    }

    public static void assertSameReservation(Reservation actual, Reservation expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
        assertThat(actual.getState().getStatus()).isEqualTo(expected.getState().getStatus());
        assertThat(actual.getState().getReservedAt()).isEqualTo(expected.getState().getReservedAt());
        assertThat(actual.getState().getUsedAt()).isEqualTo(expected.getState().getUsedAt());
        assertThat(actual.getState().getCancelledAt()).isEqualTo(expected.getState().getCancelledAt());
        assertThat(actual.getState().getExpiredAt()).isEqualTo(expected.getState().getExpiredAt());
    }
}
