package com.seatliberator.seatliberator.reservation.domain.room;

import com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegments;
import com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegmentsFixtures;

import java.time.Duration;

public class RoomOperationPolicyFixture {
    public static final Integer MAX_RESERVATION_PER_USER = 5;
    public static final Duration MAX_RESERVATION_DURATION = Duration.ofMinutes(30);
    public static final DailyTimeSegments OPERATION_TIME_SEGMENTS = DailyTimeSegmentsFixtures.get();
    public static final RoomOperationStatus OPERATION_STATUS = RoomOperationStatus.OPEN;

    public static RoomOperationPolicy get() {
        return RoomOperationPolicy.of(MAX_RESERVATION_PER_USER, MAX_RESERVATION_DURATION, OPERATION_STATUS, OPERATION_TIME_SEGMENTS);
    }

    public static class Builder {
        private Integer maxReservationPerUser = MAX_RESERVATION_PER_USER;
        private Duration maxReservationDuration = MAX_RESERVATION_DURATION;
        private RoomOperationStatus operationStatus = OPERATION_STATUS;
        private DailyTimeSegments operationTimeSegments = OPERATION_TIME_SEGMENTS;

        public Builder maxReservationPerUser(Integer maxReservationPerUser) {
            this.maxReservationPerUser = maxReservationPerUser;
            return this;
        }

        public Builder maxReservationDuration(Duration maxReservationDuration) {
            this.maxReservationDuration = maxReservationDuration;
            return this;
        }

        public Builder operationTimeSegments(DailyTimeSegments operationTimeSegments) {
            this.operationTimeSegments = operationTimeSegments;
            return this;
        }

        public Builder operationStatus(RoomOperationStatus operationStatus) {
            this.operationStatus = operationStatus;
            return this;
        }

        public RoomOperationPolicy build() {
            return RoomOperationPolicy.of(maxReservationPerUser, maxReservationDuration, operationStatus, operationTimeSegments);
        }
    }
}
