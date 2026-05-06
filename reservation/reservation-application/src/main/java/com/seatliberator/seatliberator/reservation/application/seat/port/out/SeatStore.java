package com.seatliberator.seatliberator.reservation.application.seat.port.out;

import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;

import java.util.Optional;

public interface SeatStore {

    void save(Seat seat);

    Optional<Seat> findByRoomIdAndSeatId(String roomId, String seatId);

    Optional<Seat> findForUpdate(String roomId, String seatId);

    Optional<Seat> findForUpdate(SeatLocator locator);

    void deleteByLocator(SeatLocator locator);
}
