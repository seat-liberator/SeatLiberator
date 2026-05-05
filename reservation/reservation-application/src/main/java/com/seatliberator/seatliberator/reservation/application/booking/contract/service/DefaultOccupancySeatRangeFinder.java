package com.seatliberator.seatliberator.reservation.application.booking.contract.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.OccupancySeatRangeFinder;
import com.seatliberator.seatliberator.reservation.application.booking.model.ReservationOccupancyPolicy;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationSeatOverlapCriteria;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.shared.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultOccupancySeatRangeFinder implements OccupancySeatRangeFinder {
    private final ReservationReader reader;
    private final ReservationOccupancyPolicy policy = new ReservationOccupancyPolicy();

    @Override
    public List<InstantRange> find(SeatLocator locator, InstantRange range) {
        var criteria = ReservationSeatOverlapCriteria.of(locator, range)
                .withFilter(ReservationFilter.empty().withStatuses(policy.occupyingStatuses()));

        return reader.findAllOverlapping(criteria).stream()
                .<InstantRange>map(Reservation::getRange)
                .toList();
    }
}
