package com.seatliberator.seatliberator.reservation.application.room.port.out;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;

import java.util.Optional;

public interface SeatStore {

    void save(Seat seat);

    Optional<Seat> findByRoomIdAndSeatId(String roomId, String seatId);

    Optional<Seat> findForUpdate(String roomId, String seatId);

    void deleteByLocator(SeatLocator locator);
}
