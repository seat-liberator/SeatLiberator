package com.seatliberator.seatliberator.reservation.book.application.contract.service;

import com.seatliberator.seatliberator.reservation.book.application.contract.ReservationUsageMarker;
import com.seatliberator.seatliberator.reservation.book.application.contract.result.ReservationStatusTransitionResult;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class DefaultReservationUsageMarker implements ReservationUsageMarker {
    private final ReservationReader reader;
    private final Clock clock;

    @Override
    @Transactional
    public ReservationStatusTransitionResult markUsed(Long reservationId) {
        var reservation = reader.findById(reservationId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));

        try {
            reservation.use(clock.instant());
            return ReservationStatusTransitionResult.markSuccess(reservation.getStatus());
        } catch (IllegalStateException e) {
            return ReservationStatusTransitionResult.markFail(e.getMessage(), reservation.getStatus());
        }
    }
}
