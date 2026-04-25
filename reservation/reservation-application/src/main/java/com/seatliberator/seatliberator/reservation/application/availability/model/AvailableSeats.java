package com.seatliberator.seatliberator.reservation.application.availability.model;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AvailableSeats {
    private final List<Seat> seats;

    private AvailableSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public static AvailableSeats from(List<Seat> seats, List<SeatLocator> occupiedLocators) {
        var occupied = occupiedLocators.stream()
                .map(SeatLocator::key)
                .collect(Collectors.toSet());

        var available = seats.stream()
                .filter(seat -> !occupied.contains(seat.getLocator().key()))
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
