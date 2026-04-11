package com.seatliberator.seatliberator.reservation.book.application.port.out;

import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.SeatExclusion;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatQuery {
    Optional<Seat> findByLocator(SeatLocator locator);

    List<Seat> findByRoomId(String roomId);

    boolean existsByLocator(SeatLocator locator);

    boolean existsByLocator(SeatLocator locator, SeatExclusion exclusion);
}
