package com.seatliberator.seatliberator.reservation.application.booking.contract.result;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import org.jspecify.annotations.Nullable;

public record ReservationStatusTransitionResult(
        boolean success,
        @Nullable String reason,
        ReservationStatus status
) {
    public static ReservationStatusTransitionResult markSuccess(ReservationStatus status) {
        return new ReservationStatusTransitionResult(true, null, status);
    }

    public static ReservationStatusTransitionResult markFail(String reason, ReservationStatus status) {
        return new ReservationStatusTransitionResult(false, reason, status);
    }
}
