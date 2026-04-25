package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.query.IdBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.application.booking.contract.query.ReservationLocator;
import com.seatliberator.seatliberator.reservation.application.booking.contract.query.SeatBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindMyReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindMyReservationQuery;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationRangeOverlapCriteria;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationSeatLookupCriteria;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationQueryService implements
        FindReservationUseCase,
        FindMyReservationUseCase {
    private final ReservationReader reader;

    @Override
    public ReservationResult find(ReservationLocator reservationLocator) {
        var optReservation = switch (reservationLocator) {
            case IdBasedReservationLocator(Long reservationId) -> reader.findById(reservationId);
            case SeatBasedReservationLocator(String roomId, String seatId, Instant startTime, Instant endTime) -> {
                var locator = SimpleSeatLocator.of(roomId, seatId);
                var range = SimpleTimeRange.of(startTime, endTime);
                var criteria = ReservationSeatLookupCriteria.of(locator, range)
                        .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED));
                yield reader.findOne(criteria);
            }
        };

        return optReservation
                .map(ReservationResult::of)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));
    }

    @Override
    public List<ReservationResult> find(FindMyReservationQuery query) {
        var criteria = ReservationRangeOverlapCriteria.of(query.range())
                .withFilter(
                        ReservationFilter.empty()
                                .withStatuses(query.status())
                                .withUserIds(query.userId())
                );

        return reader.findAllOverlapping(criteria).stream()
                .map(ReservationResult::of)
                .toList();
    }
}
