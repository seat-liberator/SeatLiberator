package com.seatliberator.seatliberator.reservation.book.application.service;

import com.seatliberator.seatliberator.reservation.book.application.port.in.FindReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.contract.query.IdBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.contract.query.ReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.contract.query.SeatBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReservationQueryService implements FindReservationUseCase {
    private final ReservationStore reservationStore;

    @Override
    public ReservationResult find(ReservationLocator reservationLocator) {
        var optReservation = switch (reservationLocator) {
            case IdBasedReservationLocator(Long reservationId) -> reservationStore.findById(reservationId);
            case SeatBasedReservationLocator(String roomId, String seatId, Instant startTime, Instant endTime) ->
                    reservationStore.findReservationBySeatAt(roomId, seatId, startTime, endTime);
        };

        return optReservation
                .map(ReservationResult::of)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));
    }
}
