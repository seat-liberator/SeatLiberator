package com.seatliberator.seatliberator.reservation.application.service;

import com.seatliberator.seatliberator.reservation.application.exception.ApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.exception.ApplicationException;
import com.seatliberator.seatliberator.reservation.application.port.in.ReservationUsageMarker;
import com.seatliberator.seatliberator.reservation.application.port.in.entry.ReservationStatusTransitionEntry;
import com.seatliberator.seatliberator.reservation.application.port.out.ReservationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class DefaultReservationUsageMarker implements ReservationUsageMarker {
    private final ReservationStore reservationStore;

    @Override
    @Transactional
    public ReservationStatusTransitionEntry markUsed(Long reservationId) {
        var reservation = reservationStore.findById(reservationId)
                .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.RESERVATION_NOT_FOUND));

        try {
            reservation.markUsed();
            return ReservationStatusTransitionEntry.markSuccess(reservation.getStatus());
        } catch (IllegalStateException e) {
            return ReservationStatusTransitionEntry.markFail(e.getMessage(), reservation.getStatus());
        }
    }
}
