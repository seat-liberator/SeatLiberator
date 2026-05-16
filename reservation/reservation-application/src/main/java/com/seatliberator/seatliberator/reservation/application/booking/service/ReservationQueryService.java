package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindMyReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindMyReservationQuery;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.criteria.ReservationRangeOverlapCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationQueryService implements
        FindMyReservationUseCase {
    private final ReservationReader reader;

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
