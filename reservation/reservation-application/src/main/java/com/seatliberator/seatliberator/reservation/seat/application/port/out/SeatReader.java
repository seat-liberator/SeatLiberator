package com.seatliberator.seatliberator.reservation.seat.application.port.out;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import com.seatliberator.seatliberator.reservation.seat.application.port.out.criteria.SeatExclusion;

import java.util.List;
import java.util.Optional;

public interface SeatReader {
    Optional<Seat> findByLocator(SeatLocator locator);

    List<Seat> findByRoomId(String roomId);

    boolean existsByLocator(SeatLocator locator);

    boolean existsByLocator(SeatLocator locator, SeatExclusion exclusion);
}
