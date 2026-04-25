package com.seatliberator.seatliberator.reservation.application.booking.contract;

import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationStatusTransitionResult;

public interface ReservationUsageMarker {
    ReservationStatusTransitionResult markUsed(Long reservationId);
}
