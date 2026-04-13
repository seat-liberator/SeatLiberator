package com.seatliberator.seatliberator.reservation.book.application.contract.result;

import org.jspecify.annotations.Nullable;

public record ReservationPolicyCheckResult(
        boolean reservable,
        @Nullable ReservationRejectReason rejectReason
) {
    public ReservationPolicyCheckResult {
        if (reservable && rejectReason != null) {
            throw new IllegalArgumentException("reject reason must be null when reservable flag is true");
        }

        if (!reservable && rejectReason == null) {
            throw new IllegalArgumentException("reject reason must not be null when reservable flag is false");
        }
    }

    public static ReservationPolicyCheckResult accept() {
        return new ReservationPolicyCheckResult(true, null);
    }

    public static ReservationPolicyCheckResult reject(ReservationRejectReason rejectReason) {
        return new ReservationPolicyCheckResult(false, rejectReason);
    }
}
