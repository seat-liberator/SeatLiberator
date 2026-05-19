package com.seatliberator.seatliberator.reservation.application.reservation.port.out;

import com.seatliberator.seatliberator.reservation.application.reservation.port.out.filter.ReservationFilter;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationReader {
    Optional<Reservation> findById(UUID id);

    List<Reservation> findByUserId(String userId);

    List<Reservation> findByFilter(ReservationFilter filter);
}