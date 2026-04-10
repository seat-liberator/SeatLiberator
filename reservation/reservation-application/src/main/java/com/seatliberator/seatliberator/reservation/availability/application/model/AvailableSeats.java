package com.seatliberator.seatliberator.reservation.availability.application.model;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AvailableSeats {
    private final List<Seat> seats;

    private AvailableSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public static AvailableSeats from(List<Seat> seats, List<SeatLocator> reservedLocators) {
        var occupied = reservedLocators.stream()
                .map(SimpleSeatLocator::of)
                .collect(Collectors.toSet());

        var available = seats.stream()
                .filter(seat -> !occupied.contains(SimpleSeatLocator.of(seat.getLocator())))
                .toList();

        return new AvailableSeats(available);
    }

    public List<Seat> toList() {
        return seats;
    }

    public Stream<Seat> stream() {
        return seats.stream();
    }

    public boolean isEmpty() {
        return seats.isEmpty();
    }
}
