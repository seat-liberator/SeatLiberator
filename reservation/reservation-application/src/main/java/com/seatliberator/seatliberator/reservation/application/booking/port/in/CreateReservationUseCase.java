package com.seatliberator.seatliberator.reservation.application.booking.port.in;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;

public interface CreateReservationUseCase {
    ReservationResult create(CreateReservationCommand command);
}
