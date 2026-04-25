package com.seatliberator.seatliberator.reservation.application.availability.service;

import com.seatliberator.seatliberator.reservation.application.availability.model.AvailableSeats;
import com.seatliberator.seatliberator.reservation.application.availability.model.SeatReservationStatusClassifier;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.FindAvailableSeatsUseCase;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.FindSeatOccupancyRangesUseCase;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.FindSeatStatusesUseCase;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.query.FindAvailableSeatQuery;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.query.FindOccupancyRangesQuery;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.query.FindSeatStatusesQuery;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.result.AvailableSeatResult;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.result.SeatOccupancyRangeResult;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.result.SeatStatusesResult;
import com.seatliberator.seatliberator.reservation.application.booking.contract.OccupancySeatLocatorFinder;
import com.seatliberator.seatliberator.reservation.application.booking.contract.OccupancySeatRangeFinder;
import com.seatliberator.seatliberator.reservation.application.room.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatAvailabilityService implements
        FindAvailableSeatsUseCase,
        FindSeatStatusesUseCase,
        FindSeatOccupancyRangesUseCase {
    private final SeatReader seatReader;
    private final OccupancySeatLocatorFinder occupancySeatLocatorFinder;
    private final OccupancySeatRangeFinder occupancySeatRangeFinder;

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

        var occupiedLocators = occupancySeatLocatorFinder.find(roomId, range);

        var statuses = SeatReservationStatusClassifier.from(seatLocators, occupiedLocators).toMap();

        return seatLocators.stream()
                .map(locator ->
                        SeatStatusesResult.of(locator, statuses.get(locator.key()))
                )
                .toList();
    }

    @Override
    public List<SeatOccupancyRangeResult> find(FindOccupancyRangesQuery query) {
        var targetLocator = query.locator();
        var targetRange = query.range();

        var exists = seatReader.existsByLocator(targetLocator);
        if (!exists) throw new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        var occupiedRanges = occupancySeatRangeFinder.find(targetLocator, targetRange);

        return occupiedRanges.stream()
                .map(SeatOccupancyRangeResult::of)
                .toList();
    }
}
