package com.seatliberator.seatliberator.reservation.domain.fixture;

import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.persistence.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.persistence.RoomOperationStatus;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;

public class RoomOperationPolicyFixture {
    public static final Integer MAX_RESERVATION_PER_USER = 5;
    public static final Duration MAX_RESERVATION_DURATION = Duration.ofMinutes(30);
    public static final TimeRange OPERATION_RANGE = createRange(
            TestSupport.fixedClock.instant(),
            TestSupport.fixedClock.instant().plus(12, ChronoUnit.HOURS)
    );
    public static final RoomOperationStatus OPERATION_STATUS = RoomOperationStatus.OPEN;

    public static RoomOperationPolicy get() {
        return RoomOperationPolicy.of(MAX_RESERVATION_PER_USER, MAX_RESERVATION_DURATION, OPERATION_STATUS, OPERATION_RANGE);
    }

    public static class Builder {
        private Integer maxReservationPerUser = MAX_RESERVATION_PER_USER;
        private Duration maxReservationDuration = MAX_RESERVATION_DURATION;
        private RoomOperationStatus operationStatus = OPERATION_STATUS;
        private TimeRange operationRange = OPERATION_RANGE;

        public Builder maxReservationPerUser(Integer maxReservationPerUser) {
            this.maxReservationPerUser = maxReservationPerUser;
            return this;
        }

        public Builder maxReservationDuration(Duration maxReservationDuration) {
            this.maxReservationDuration = maxReservationDuration;
            return this;
        }

        public Builder operationRange(TimeRange operationRange) {
            this.operationRange = operationRange;
            return this;
        }

        public Builder operationStatus(RoomOperationStatus operationStatus) {
            this.operationStatus = operationStatus;
            return this;
        }

        public RoomOperationPolicy build() {
            return RoomOperationPolicy.of(maxReservationPerUser, maxReservationDuration, operationStatus, operationRange);
        }
    }
}
