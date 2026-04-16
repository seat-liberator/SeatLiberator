package com.seatliberator.seatliberator.reservation.availability.application.service;

import com.seatliberator.seatliberator.reservation.availability.application.model.AvailableSeats;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.FindAvailableSeatUseCase;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.query.FindAvailableSeatQuery;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.result.AvailableSeatResult;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationRoomOverlapCriteria;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;
import com.seatliberator.seatliberator.reservation.seat.application.port.out.SeatReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailableSeatService implements FindAvailableSeatUseCase {
    private final ReservationReader reservationReader;
    private final SeatReader seatReader;

    @Override
    public List<AvailableSeatResult> findAvailabilitySeats(FindAvailableSeatQuery query) {
        var roomId = query.roomId();
        var range = query.range();
        var seats = seatReader.findByRoomId(roomId);

        if (seats.isEmpty()) return List.of();

        var criteria = ReservationRoomOverlapCriteria.of(roomId, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED, ReservationStatus.USED));
        var reservedLocators = reservationReader.findAllOverlapping(criteria).stream()
                .<SeatLocator>map(Reservation::getLocator)
                .toList();

        return AvailableSeats.from(seats, reservedLocators).stream()
                .map(AvailableSeatResult::from)
                .toList();
    }
}
