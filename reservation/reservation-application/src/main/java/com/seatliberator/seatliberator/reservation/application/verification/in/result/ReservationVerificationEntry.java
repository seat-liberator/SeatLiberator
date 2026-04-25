package com.seatliberator.seatliberator.reservation.application.verification.in.result;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;

public record ReservationVerificationEntry(
        ReservationResult entry
) implements VerificationEntry {
    public static ReservationVerificationEntry of(Reservation reservation) {
        var entry = ReservationResult.of(reservation);
        return new ReservationVerificationEntry(entry);
    }
}
