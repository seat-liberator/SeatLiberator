package com.seatliberator.seatliberator.reservation.application.booking.contract;

import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatorCommand;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;

public interface ReservationCreator {
    Reservation create(ReservationCreatorCommand command);
}
