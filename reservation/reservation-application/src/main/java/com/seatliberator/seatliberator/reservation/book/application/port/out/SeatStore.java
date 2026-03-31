package com.seatliberator.seatliberator.reservation.book.application.port.out;

import com.seatliberator.seatliberator.reservation.domain.Seat;
import com.seatliberator.seatliberator.reservation.shared.domain.SeatLocator;

import java.util.Optional;

public interface SeatStore {

    void save(Seat seat);

    Optional<Seat> findByRoomIdAndSeatId(String roomId, String seatId);

    Optional<Seat> findByLocator(SeatLocator locator);

    Optional<Seat> findForUpdate(String roomId, String seatId);

    void deleteByRoomIdAndSeatId(String roomId, String seatId);

    boolean existsSeatConflict(String roomId, String seatId);

    boolean existsSeatConflictExcept(Long id, String roomId, String seatId);

}
