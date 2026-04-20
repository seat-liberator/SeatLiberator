package com.seatliberator.seatliberator.reservation.book.application.contract.service;

import com.seatliberator.seatliberator.reservation.book.application.contract.OccupancySeatLocatorFinder;
import com.seatliberator.seatliberator.reservation.book.application.model.ReservationOccupancyPolicy;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationRoomOverlapCriteria;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultOccupancySeatLocatorFinder implements OccupancySeatLocatorFinder {
    private final ReservationReader reader;
    private final ReservationOccupancyPolicy policy = new ReservationOccupancyPolicy();

    @Override
    public List<SeatLocator> find(String roomId, TimeRange range) {
        var criteria = ReservationRoomOverlapCriteria.of(roomId, range)
                .withFilter(ReservationFilter.empty().withStatuses(policy.occupyingStatuses()));

        return reader.findAllOverlapping(criteria).stream()
                .<SeatLocator>map(Reservation::getLocator)
                .toList();
    }
}
