package com.seatliberator.seatliberator.reservation.book.application.port.out;

import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationOverlapCriteria;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationRoomOverlapCriteria;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationFindOneCriteria;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationReader {
    Optional<Reservation> findById(Long id);
    Optional<Reservation> findByUserId(String userId);

    boolean existsOne(ReservationFindOneCriteria criteria);

    Optional<Reservation> findOne(ReservationFindOneCriteria criteria);

    boolean existsOverlapping(ReservationOverlapCriteria criteria);

    boolean existsOverlapping(ReservationRoomOverlapCriteria criteria);

    List<Reservation> findAllOverlapping(ReservationOverlapCriteria criteria);

    List<Reservation> findAllOverlapping(ReservationRoomOverlapCriteria criteria);
}