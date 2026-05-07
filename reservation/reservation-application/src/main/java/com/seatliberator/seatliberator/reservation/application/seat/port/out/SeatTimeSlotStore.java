package com.seatliberator.seatliberator.reservation.application.seat.port.out;

import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;

public interface SeatTimeSlotStore {
    SeatTimeSlot save(SeatTimeSlot seatTimeSlot);

    void delete(SeatTimeSlot seatTimeSlot);
}
