package com.seatliberator.seatliberator.reservation.book.application.contract;

import com.seatliberator.seatliberator.reservation.book.application.contract.result.ReservationStatusTransitionResult;

public interface ReservationUsageMarker {
    ReservationStatusTransitionResult markUsed(Long reservationId);
}
