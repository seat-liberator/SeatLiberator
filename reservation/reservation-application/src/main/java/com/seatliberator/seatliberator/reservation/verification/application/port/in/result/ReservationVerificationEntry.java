package com.seatliberator.seatliberator.reservation.verification.application.port.in.result;

import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationEntry;
import com.seatliberator.seatliberator.reservation.book.domain.Reservation;

public record ReservationVerificationEntry(
        ReservationEntry entry
) implements VerificationEntry {
    public static ReservationVerificationEntry of(Reservation reservation) {
        var entry = ReservationEntry.of(reservation);
        return new ReservationVerificationEntry(entry);
    }
}
