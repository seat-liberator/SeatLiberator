package com.seatliberator.seatliberator.reservation.application.port.in.entry;

import org.jspecify.annotations.Nullable;

public record ReservationStatusTransitionEntry(
        boolean success,
        @Nullable String reason
) {
    public static ReservationStatusTransitionEntry markSuccess() {
        return new ReservationStatusTransitionEntry(true, null);
    }

    public static ReservationStatusTransitionEntry markFail(String reason) {
        return new ReservationStatusTransitionEntry(false, reason);
    }
}
