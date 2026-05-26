package com.seatliberator.seatliberator.reservation.application.seat.port.out;

import com.seatliberator.seatliberator.reservation.domain.seat.Seat;

public interface SeatStore {
    Seat save(Seat seat);

    void delete(Seat seat);
}
