package com.seatliberator.seatliberator.reservation.availability.application.service;

import com.seatliberator.seatliberator.reservation.availability.application.model.AvailableSeats;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.SeatAvailabilityReader;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.entry.AvailabilitySeatEntry;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationQuery;
import com.seatliberator.seatliberator.reservation.book.application.port.out.SeatQuery;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultSeatAvailabilityReader implements SeatAvailabilityReader {
    private final ReservationQuery reservationQuery;
    private final SeatQuery seatQuery;

    @Override
    public List<AvailabilitySeatEntry> findAvailabilitySeats(String roomId, TimeRange range) {
        var seats = seatQuery.findByRoomId(roomId);

        if (seats.isEmpty()) return List.of();

        var reservedLocators = reservationQuery.findAllOverlappingInRoom(roomId, range).stream()
                .<SeatLocator>map(Reservation::getLocator)
                .toList();

        return AvailableSeats.from(seats, reservedLocators).stream()
                .map(AvailabilitySeatEntry::from)
                .toList();
    }
}
