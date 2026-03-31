package com.seatliberator.seatliberator.reservation.book.application.port.in.entry;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import org.jspecify.annotations.Nullable;

public record ReservationStatusTransitionEntry(
        boolean success,
        @Nullable String reason,
        ReservationStatus status
) {
    public static ReservationStatusTransitionEntry markSuccess(ReservationStatus status) {
        return new ReservationStatusTransitionEntry(true, null, status);
    }

    public static ReservationStatusTransitionEntry markFail(String reason, ReservationStatus status) {
        return new ReservationStatusTransitionEntry(false, reason, status);
    }
}
