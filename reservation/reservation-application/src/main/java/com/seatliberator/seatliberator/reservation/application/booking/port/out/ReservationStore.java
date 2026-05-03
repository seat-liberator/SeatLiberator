package com.seatliberator.seatliberator.reservation.application.booking.port.out;

import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;

public interface ReservationStore {
    Reservation save(Reservation reservation);

    void delete(Reservation reservation);
}
