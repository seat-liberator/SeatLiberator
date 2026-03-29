package com.seatliberator.seatliberator.reservation.book.application.port.out;

import com.seatliberator.seatliberator.reservation.book.domain.Seat;

import java.util.Optional;

public interface SeatStore {

    void save(Seat seat);

    Optional<Seat> findByRoomIdAndSeatId(String roomId, String seatId);

    Optional<Seat> findForUpdate(String roomId, String seatId);

    void deleteByRoomIdAndSeatId(String roomId, String seatId);

    boolean existsSeatConflict(String roomId, String seatId);

    boolean existsSeatConflictExcept(Long id, String roomId, String seatId);

}
