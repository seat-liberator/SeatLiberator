package com.seatliberator.seatliberator.reservation.book.application.port.out;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;

import java.util.Collection;
import java.util.Optional;

public interface SeatStore {

    void save(Seat seat);

    Optional<Seat> findByRoomIdAndSeatId(String roomId, String seatId);

    Optional<Seat> findByLocator(SeatLocator locator);

    Optional<Seat> findForUpdate(String roomId, String seatId);

    void deleteByRoomIdAndSeatId(String roomId, String seatId);

    void deleteByLocator(SeatLocator locator);

    boolean existsSeatConflict(String roomId, String seatId);

    boolean existsByLocator(SeatLocator locator);

    boolean existsSeatConflictExcept(Long id, String roomId, String seatId);

    boolean existsByLocatorWithExcludeIds(SeatLocator locator, Collection<Long> ids);

}
