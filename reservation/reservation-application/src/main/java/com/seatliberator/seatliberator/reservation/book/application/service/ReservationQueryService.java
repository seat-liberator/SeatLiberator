package com.seatliberator.seatliberator.reservation.book.application.service;

import com.seatliberator.seatliberator.reservation.book.application.contract.query.IdBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.contract.query.ReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.contract.query.SeatBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.port.in.FindReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationFindOneCriteria;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReservationQueryService implements FindReservationUseCase {
    private final ReservationReader reader;

    @Override
    public ReservationResult find(ReservationLocator reservationLocator) {
        var optReservation = switch (reservationLocator) {
            case IdBasedReservationLocator(Long reservationId) -> reader.findById(reservationId);
            case SeatBasedReservationLocator(String roomId, String seatId, Instant startTime, Instant endTime) -> {
                var locator = SimpleSeatLocator.from(roomId, seatId);
                var range = SimpleTimeRange.from(startTime, endTime);
                var criteria = ReservationFindOneCriteria.of(locator, range)
                        .withStatuses(ReservationStatus.RESERVED);
                yield reader.findOne(criteria);
            }
        };

        return optReservation
                .map(ReservationResult::of)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));
    }
}
