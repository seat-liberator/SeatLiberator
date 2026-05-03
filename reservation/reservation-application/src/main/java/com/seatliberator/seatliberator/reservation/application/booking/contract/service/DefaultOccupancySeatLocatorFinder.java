package com.seatliberator.seatliberator.reservation.application.booking.contract.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.OccupancySeatLocatorFinder;
import com.seatliberator.seatliberator.reservation.application.booking.model.ReservationOccupancyPolicy;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationRoomOverlapCriteria;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;
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
