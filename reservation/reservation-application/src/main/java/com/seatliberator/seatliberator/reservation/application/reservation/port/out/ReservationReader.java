package com.seatliberator.seatliberator.reservation.application.reservation.port.out;

import com.seatliberator.seatliberator.reservation.application.reservation.port.out.criteria.*;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationReader {
    Optional<Reservation> findById(UUID id);

    List<Reservation> findByUserId(String userId);

    boolean existsOne(ReservationSeatLookupCriteria criteria);

    Optional<Reservation> findOne(ReservationSeatLookupCriteria criteria);

    boolean existsOverlapping(ReservationSeatOverlapCriteria criteria);

    boolean existsOverlapping(ReservationRoomOverlapCriteria criteria);

    List<Reservation> findAllOverlapping(ReservationSeatOverlapCriteria criteria);

    List<Reservation> findAllOverlapping(ReservationRangeOverlapCriteria criteria);

    List<Reservation> findAllOverlapping(ReservationRoomOverlapCriteria criteria);

    List<Reservation> findByFilter(ReservationFilter filter);
}