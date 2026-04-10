package com.seatliberator.seatliberator.reservation.book.application.port.out;

import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationCriteria;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationExclusion;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationTarget;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationQuery {
    Optional<Reservation> findById(Long id);

    Optional<Reservation> findByUserId(String userId);

    Optional<Reservation> findOne(ReservationCriteria criteria);

    boolean existsOverlapping(ReservationTarget target);

    boolean existsOverlapping(ReservationTarget target, ReservationExclusion exclusion);

    List<Reservation> findAllOverlapping(TimeRange range);

    List<Reservation> findAllOverlappingInRoom(String roomId, TimeRange range);
}