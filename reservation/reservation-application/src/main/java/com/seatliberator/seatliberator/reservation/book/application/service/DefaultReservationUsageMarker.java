package com.seatliberator.seatliberator.reservation.book.application.service;

import com.seatliberator.seatliberator.reservation.book.application.exception.BookApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.book.application.exception.BookApplicationException;
import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationUsageMarker;
import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationStatusTransitionEntry;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultReservationUsageMarker implements ReservationUsageMarker {
    private final ReservationStore reservationStore;

    @Override
    @Transactional
    public ReservationStatusTransitionEntry markUsed(Long reservationId) {
        var reservation = reservationStore.findById(reservationId)
                .orElseThrow(() -> new BookApplicationException(BookApplicationErrorCode.RESERVATION_NOT_FOUND));

        try {
            reservation.markUsed();
            return ReservationStatusTransitionEntry.markSuccess(reservation.getStatus());
        } catch (IllegalStateException e) {
            return ReservationStatusTransitionEntry.markFail(e.getMessage(), reservation.getStatus());
        }
    }
}
