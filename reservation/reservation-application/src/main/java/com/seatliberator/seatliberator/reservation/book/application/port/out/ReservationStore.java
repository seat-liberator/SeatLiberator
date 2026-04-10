package com.seatliberator.seatliberator.reservation.book.application.port.out;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

public interface ReservationStore {
    Reservation save(Reservation reservation);

    Optional<Reservation> findById(Long reservationId);

    Optional<Reservation> findByUserId(String userId);

    Optional<Reservation> findReservationBySeatAt(String roomId, String seatId, Instant startTime, Instant endTime);

    Optional<Reservation> findByLocatorAndRangeAndStatus(SeatLocator locator, TimeRange range, ReservationStatus status);

    void delete(Reservation reservation);

    boolean existsByLocatorAndRange(SeatLocator locator, TimeRange range);

    boolean existsByLocatorAndRangeAndStatus(SeatLocator locator, TimeRange range, ReservationStatus status);

    boolean existsByLocatorAndRangeWithExcludeIds(SeatLocator locator, TimeRange range, Collection<Long> ids);
}
