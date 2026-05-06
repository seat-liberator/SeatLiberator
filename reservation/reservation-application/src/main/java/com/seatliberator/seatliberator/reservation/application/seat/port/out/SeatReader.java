package com.seatliberator.seatliberator.reservation.application.seat.port.out;

import com.seatliberator.seatliberator.reservation.application.seat.port.out.criteria.SeatExclusion;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;

import java.util.List;
import java.util.Optional;

public interface SeatReader {
    Optional<Seat> findByLocator(SeatLocator locator);

    List<Seat> findByRoomId(String roomId);

    boolean existsByLocator(SeatLocator locator);

    boolean existsByLocator(SeatLocator locator, SeatExclusion exclusion);
}
