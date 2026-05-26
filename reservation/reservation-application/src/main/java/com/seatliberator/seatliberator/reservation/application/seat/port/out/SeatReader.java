package com.seatliberator.seatliberator.reservation.application.seat.port.out;

import com.seatliberator.seatliberator.reservation.application.seat.port.out.criteria.SeatLookupCriteria;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatFilter;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatReader {
    boolean existsById(UUID id);

    boolean existsByCriteria(SeatLookupCriteria criteria);

    Optional<Seat> findById(UUID id);

    List<Seat> findByFilter(SeatFilter filter);
}
