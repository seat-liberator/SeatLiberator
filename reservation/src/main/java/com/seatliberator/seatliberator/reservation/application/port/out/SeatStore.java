package com.seatliberator.seatliberator.reservation.application.port.out;

import com.seatliberator.seatliberator.reservation.domain.Seat;

import java.util.Optional;

public interface SeatStore {

    Seat save(Seat seat);

    Optional<Seat> findByRoomIdAndSeatId(String roomId, String seatId);

    void deleteByRoomIdAndSeatId(String roomId, String seatId);

    boolean existsSeatConflict(String roomId, String seatId);

    boolean existsSeatConflictExcept(String roomId, String seatId);

}
