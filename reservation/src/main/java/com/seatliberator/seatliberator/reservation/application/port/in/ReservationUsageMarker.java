package com.seatliberator.seatliberator.reservation.application.port.in;

import com.seatliberator.seatliberator.reservation.application.port.in.entry.ReservationStatusTransitionEntry;

public interface ReservationUsageMarker {
    ReservationStatusTransitionEntry markUsed(Long reservationId);
}
