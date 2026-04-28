package com.seatliberator.seatliberator.reservation.application.verification.port.in.result;

import java.time.Instant;

public record UseReservationResult(
        boolean accept,
        String rejectReason,
        Instant processedAt
) {
    public UseReservationResult {
        if (processedAt == null) throw new IllegalArgumentException("processedAt must not be null.");

        if (accept) {
            if (rejectReason != null)
                throw new IllegalArgumentException("rejectReason must be null when accept is true");
        } else {
            if (rejectReason == null || rejectReason.isBlank())
                throw new IllegalArgumentException("rejectReason must not be null or blank when accept is false.");
        }
    }

    public static UseReservationResult accept(Instant processedAt) {
        return new UseReservationResult(true, null, processedAt);
    }

    public static UseReservationResult reject(String rejectReason, Instant processedAt) {
        return new UseReservationResult(false, rejectReason, processedAt);
    }
}
