package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationStatusTransitionEntry;

public interface ReservationUsageMarker {
    ReservationStatusTransitionEntry markUsed(Long reservationId);
}
