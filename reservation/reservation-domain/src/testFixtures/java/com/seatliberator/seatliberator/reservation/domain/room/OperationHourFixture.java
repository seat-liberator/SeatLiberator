package com.seatliberator.seatliberator.reservation.domain.room;

import java.time.LocalTime;

public class OperationHourFixture {
    public static final LocalTime OPEN_AT = LocalTime.of(6, 0);
    public static final LocalTime CLOSE_AT = LocalTime.of(0, 0);

    public static OperationHours get() {
        return OperationHours.of(OPEN_AT, CLOSE_AT);
    }

    public static class Builder {
        private LocalTime openAt = OPEN_AT;
        private LocalTime closeAt = CLOSE_AT;

        public Builder openAt(LocalTime openAt) {
            this.openAt = openAt;
            return this;
        }

        public Builder closeAt(LocalTime closeAt) {
            this.closeAt = closeAt;
            return this;
        }

        public OperationHours build() {
            return OperationHours.of(openAt, closeAt);
        }
    }
}
