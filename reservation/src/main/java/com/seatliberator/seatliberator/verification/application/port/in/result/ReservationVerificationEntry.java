package com.seatliberator.seatliberator.verification.application.port.in.result;

import com.seatliberator.seatliberator.reservation.application.port.in.entry.ReservationEntry;
import com.seatliberator.seatliberator.reservation.domain.Reservation;

public record ReservationVerificationEntry(
        ReservationEntry entry
) implements VerificationEntry {
    public static ReservationVerificationEntry of(Reservation reservation) {
        var entry = ReservationEntry.of(reservation);
        return new ReservationVerificationEntry(entry);
    }
}
