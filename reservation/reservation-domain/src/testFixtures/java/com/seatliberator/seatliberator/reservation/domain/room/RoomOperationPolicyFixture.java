package com.seatliberator.seatliberator.reservation.domain.room;

import com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegment;
import com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegmentFixtures;

import java.time.Duration;

public class RoomOperationPolicyFixture {
    public static final Integer MAX_RESERVATION_PER_USER = 5;
    public static final Duration MAX_RESERVATION_DURATION = Duration.ofMinutes(30);
    public static final DailyTimeSegment OPERATION_HOURS = DailyTimeSegmentFixtures.get();
    public static final RoomOperationStatus OPERATION_STATUS = RoomOperationStatus.OPEN;

    public static RoomOperationPolicy get() {
        return RoomOperationPolicy.of(MAX_RESERVATION_PER_USER, MAX_RESERVATION_DURATION, OPERATION_STATUS, OPERATION_HOURS);
    }

    public static class Builder {
        private Integer maxReservationPerUser = MAX_RESERVATION_PER_USER;
        private Duration maxReservationDuration = MAX_RESERVATION_DURATION;
        private RoomOperationStatus operationStatus = OPERATION_STATUS;
        private DailyTimeSegment operationHours = OPERATION_HOURS;

        public Builder maxReservationPerUser(Integer maxReservationPerUser) {
            this.maxReservationPerUser = maxReservationPerUser;
            return this;
        }

        public Builder maxReservationDuration(Duration maxReservationDuration) {
            this.maxReservationDuration = maxReservationDuration;
            return this;
        }

        public Builder operationHours(DailyTimeSegment operationHours) {
            this.operationHours = operationHours;
            return this;
        }

        public Builder operationStatus(RoomOperationStatus operationStatus) {
            this.operationStatus = operationStatus;
            return this;
        }

        public RoomOperationPolicy build() {
            return RoomOperationPolicy.of(maxReservationPerUser, maxReservationDuration, operationStatus, operationHours);
        }
    }
}
