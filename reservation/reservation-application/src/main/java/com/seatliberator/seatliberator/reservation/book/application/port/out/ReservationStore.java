package com.seatliberator.seatliberator.reservation.book.application.port.out;

import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;

public interface ReservationStore {
    Reservation save(Reservation reservation);

    void delete(Reservation reservation);
}
