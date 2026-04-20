package com.seatliberator.seatliberator.reservation.availability.application.service;

import com.seatliberator.seatliberator.reservation.availability.application.model.AvailableSeats;
import com.seatliberator.seatliberator.reservation.availability.application.model.SeatReservationStatusClassifier;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.FindAvailableSeatsUseCase;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.FindSeatStatusesUseCase;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.query.FindAvailableSeatQuery;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.query.FindSeatStatusesQuery;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.result.AvailableSeatResult;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.result.SeatStatusesResult;
import com.seatliberator.seatliberator.reservation.book.application.contract.OccupancySeatLocatorFinder;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import com.seatliberator.seatliberator.reservation.seat.application.port.out.SeatReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatAvailabilityService implements
        FindAvailableSeatsUseCase,
        FindSeatStatusesUseCase {
    private final SeatReader seatReader;
    private final OccupancySeatLocatorFinder occupancySeatLocatorFinder;

    @Override
    public List<AvailableSeatResult> find(FindAvailableSeatQuery query) {
        var roomId = query.roomId();
        var range = query.range();
        var seats = seatReader.findByRoomId(roomId);

        if (seats.isEmpty()) return List.of();

        var occupancyReservations = occupancySeatLocatorFinder.find(roomId, range);

        return AvailableSeats.from(seats, occupancyReservations).stream()
                .map(AvailableSeatResult::from)
                .toList();
    }

    @Override
    public List<SeatStatusesResult> find(FindSeatStatusesQuery query) {
        var roomId = query.roomId();
        var range = query.range();
        var seatLocators = seatReader.findByRoomId(roomId).stream()
                .map(Seat::getLocator)
                .toList();

        if (seatLocators.isEmpty()) return List.of();

        var occupancyReservations = occupancySeatLocatorFinder.find(roomId, range);

        var statuses = SeatReservationStatusClassifier.from(seatLocators, occupancyReservations).toMap();

        return seatLocators.stream()
                .map(locator ->
                        SeatStatusesResult.of(locator, statuses.get(locator.key()))
                )
                .toList();
    }
}
